package ru.ashkart;

import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static AtomicInteger linksRemoved = new AtomicInteger();

    private static long offset = 0;

    public static void main(String[] args) {
        linksRemoved.set(0);

        ConnectionService connectionService = Services.getContainer().getConnectionService();

        for (int i = 0; i < args.length; i++) {
            if ("offset".equals(args[i]) && i+1 < args.length) {
                offset = Long.parseLong(args[i+1]);
            }
        }

        var connectionLinksToRemove = connectionService.getConnection();
        var connectionLinks = connectionService.getConnection();

        try {
            connectionLinksToRemove.setAutoCommit(false);
            connectionLinksToRemove.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);

            Statement createCursorStmt = connectionLinksToRemove.createStatement();
            createCursorStmt.execute("declare links_to_remove_cursor NO scroll cursor with hold for select ltr.link_id from shorter.public.links_to_remove ltr;");
            createCursorStmt.close();

            Statement st = connectionLinksToRemove.createStatement(
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY
            );

            connectionLinks.setAutoCommit(false);
            connectionLinks.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);

            processBatch(connectionLinks, st);

            st.close();
            connectionLinks.close();
            connectionLinksToRemove.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private static void processBatch(Connection connectionLinks, Statement st) {
        LinkService linkService = Services.getContainer().getLinkService();

        var executorService = new BlockingExecutor(1, Executors.newFixedThreadPool(1));

        try {
            st.execute("MOVE FORWARD " + offset + " from links_to_remove_cursor;");

            var queryFetch = "FETCH links_to_remove_cursor;";
            var rs = st.executeQuery(queryFetch);

            while (rs.next()) {
                var linkId = rs.getLong("link_id");

                if (linkId == 0L) {
                    break;
                }

                executorService.execute(() -> {
                    linkService.deleteLink(connectionLinks, linkId);
                    linksRemoved.set(linksRemoved.get() + 1);
                    if ((linksRemoved.get() % 1000) == 0) {
                        System.out.println(String.format("Links removed: %d", linksRemoved.get()));
                    }
                });

                rs = st.executeQuery(queryFetch);
            }

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);

            if (!executorService.isTerminated()) {
                executorService.shutdownNow();
            }

            st.executeQuery("close links_to_remove_cursor;");

            rs.close();
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
