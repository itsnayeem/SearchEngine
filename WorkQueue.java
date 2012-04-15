import java.util.LinkedList;

/**
 * WorkQueue takes Runnable objects and processes them using one of the threads
 * created. When thread is completed it will go for more work. If no work exists
 * thread will wait on queue.
 * 
 * @author snelrahman
 * 
 */
public class WorkQueue {
	private final int nThreads = 10;
	private final PoolWorker[] threads;
	private final LinkedList<Runnable> queue;
	private volatile boolean shutdown;
	private Object messenger = new Object();
	private static WorkQueue instance = null;

	/**
	 * 
	 * @param num
	 *            Number of threads to run
	 */
	public WorkQueue() {
		threads = new PoolWorker[nThreads];
		queue = new LinkedList<Runnable>();
		shutdown = false;

		for (int i = 0; i < nThreads; i++) {
			threads[i] = new PoolWorker(i);
			threads[i].start();
		}
	}

	/**
	 * Static method to return instance. Singleton Pattern.
	 * 
	 * @return single instance of WorkQueue
	 */
	public static WorkQueue getInstance() {
		if (instance == null) {
			synchronized (WorkQueue.class) {
				if (instance == null) {
					instance = new WorkQueue();
				}
			}
		}
		return instance;
	}

	/**
	 * Adds runnable object to queue
	 * 
	 * @param r
	 *            Runnable object
	 */
	public void execute(Runnable r) {
		synchronized (queue) {
			queue.addLast(r);
			queue.notify();
		}
	}

	/**
	 * Check to see if all the threads are in waiting state, which indicates
	 * that there is no work in the queue
	 * 
	 * @return whether or not all threads are waiting
	 */
	public boolean isAllWaiting() {

		boolean retval = true;
		for (PoolWorker p : threads) {
			if (p.getState() != Thread.State.WAITING) {
				// System.out.println(p.getId() + " NOT is waiting");
				retval = false;
			} else {
				// System.out.println(p.getId() + " is waiting");
			}
		}
		return retval;
	}

	/**
	 * Called after all work is put into the queue. Method will block until all
	 * threads are completed and will signal them to cleanly exit
	 */
	public void shutdownQueue() {
		synchronized (messenger) {
			while (!isAllWaiting()) {
				try {
					// System.out.println("main is waiting on messenger");
					messenger.wait();
				} catch (InterruptedException e) {
				}
				Thread.yield();
			}
		}
		shutdown = true;
		synchronized (queue) {

			// System.out.println("main is notifying queue");
			queue.notifyAll();
		}
		for (PoolWorker p : threads) {
			try {
				p.join();
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Thread that will work with WorkQueue
	 * 
	 * Instead of exiting on completion, PoolWorker will check queue for more
	 * work. If no work is found, PoolWorker will wait on queue.
	 * 
	 * If shutdown mode is active, PoolWorker will exit cleanly.
	 * 
	 * @author snelrahman
	 * 
	 */
	private class PoolWorker extends Thread {
		PoolWorker(int num) {
			super("PoolWorker Number: " + num);
		}

		public void run() {
			Runnable r;

			while (true) {
				synchronized (queue) {
					while (queue.isEmpty()) {
						if (shutdown)
							return;
						synchronized (messenger) {
							// System.out.println("PoolWorker" + this.getId()
							// + " is notifying messenger");
							messenger.notifyAll();
						}
						try {

							// System.out.println("PoolWorker" + this.getId()
							// + " is waiting on queue");
							queue.wait();
						} catch (InterruptedException ignored) {
						}
					}

					r = queue.removeFirst();
				}

				try {
					r.run();
				} catch (RuntimeException e) {
					System.out.println("******RuntimeException on thread: "
							+ this.getId());
				}
			}
		}
	}
}