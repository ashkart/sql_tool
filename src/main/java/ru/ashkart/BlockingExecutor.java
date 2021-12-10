package ru.ashkart;

import java.util.List;
import java.util.concurrent.*;

class BlockingExecutor extends AbstractExecutorService {

  final Semaphore semaphore;
  final ExecutorService delegate;

  public BlockingExecutor(final int concurrentTasksLimit, final ExecutorService delegate) {
    semaphore = new Semaphore(concurrentTasksLimit);
    this.delegate = delegate;
  }

  @Override
  public void execute(final Runnable command) {
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }

    final Runnable wrapped = () -> {
      try {
        command.run();
      } finally {
        semaphore.release();
      }
    };

    delegate.execute(wrapped);

  }

  @Override
  public void shutdown() {
    this.delegate.shutdown();
  }

  @Override
  public List<Runnable> shutdownNow() {
    return this.delegate.shutdownNow();
  }

  @Override
  public boolean isShutdown() {
    return this.delegate.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return this.delegate.isTerminated();
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return this.delegate.awaitTermination(timeout, unit);
  }
}
