/**
 * Lock that allows multiple reads but only single writer at the same time.
 * 
 * @author snelrahman
 * 
 */
public class MultiReadSingleWriteLock {
	private volatile int readers;
	private volatile boolean writeLock;

	public MultiReadSingleWriteLock() {
		readers = 0;
		writeLock = false;
	}

	/**
	 * Will give one read lock and increase number of readers
	 */
	public synchronized void getReadLock() {
		while (writeLock) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		readers++;
	}

	/**
	 * Will release one lock and notify all waiters of change
	 */
	public synchronized void releaseReadLock() {
		readers--;
		notifyAll();
	}

	/**
	 * If there are no readers or writers, will give write lock
	 */
	public synchronized void getWriteLock() {
		while (writeLock || readers > 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		writeLock = true;
	}

	/**
	 * Will release write lock and notify all waiters of change
	 */
	public synchronized void releaseWriteLock() {
		writeLock = false;
		notifyAll();
	}
}
