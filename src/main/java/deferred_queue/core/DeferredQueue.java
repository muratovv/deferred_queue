package deferred_queue.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class provide queue with time deferred behavior
 */
@SuppressWarnings("WeakerAccess")
public class DeferredQueue<T> {
    /**
     * Maximal size available in queue, after exceed called {@link DeferredQueue#onForceDequeCallback}
     */
    private final int MAX_QUEUE_SIZE;

    /**
     * Callback called when time of element in queue expired
     * or on call {@link DeferredQueue#forceTimePull()}
     */
    private Callback<T> onTimeExpiredCallback = new EmptyCallback<>();

    /**
     * Callback called when element forced removed from queue,
     * by client code with {@link DeferredQueue#forcePull()}
     * or on queue overflow.
     */
    private Callback<T> onForceDequeCallback = new EmptyCallback<>();

    private Lock            lock;
    private ExecutorService executorService;

    private ArrayList<Stamped<T>> storage;

    /**
     * @param capacity        maximum size of queue
     * @param executorService service responsible for execute background pull task
     */
    public DeferredQueue(int capacity, ExecutorService executorService) {
        this.MAX_QUEUE_SIZE = capacity;
        storage = new ArrayList<>(capacity);
        this.lock = new ReentrantLock(false);
        this.executorService = executorService;
    }

    public DeferredQueue() {
        this(1, Executors.newSingleThreadExecutor());
    }

    /**
     * Setter for {@link DeferredQueue#onTimeExpiredCallback}
     */
    public void setOnTimeExpiredCallback(Callback<T> onTimeDequeCallback) {
        try {
            lock.lock();
            this.onTimeExpiredCallback = onTimeDequeCallback;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Setter for {@link DeferredQueue#onForceDequeCallback}
     */
    public void setOnForceDequeCallback(Callback<T> onForceDequeCallback) {
        try {
            lock.lock();
            this.onForceDequeCallback = onForceDequeCallback;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Insert {@code value} to queue
     *
     * @param value target object
     * @param delay after it, {@value} will be polled to {@link DeferredQueue#onTimeExpiredCallback}
     */
    public void insert(T value, Delay delay) {
        try {
            lock.lock();

            tryForcePull();
            add(value, delay, Stamped.now());
            sort();
            submitBackgroundTask();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Force deque element and call {@link DeferredQueue#onTimeExpiredCallback}
     */
    public void forceTimePull() {
        try {
            lock.lock();

            internalTimePull();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Force deque element with call {@link DeferredQueue#onForceDequeCallback}
     */
    public void forcePull() {
        try {
            lock.lock();

            internalForcePull();
        } finally {
            lock.unlock();
        }
    }

    /**
     * shutdown {@link ExecutorService} for end work
     */
    public void stopService() {
        executorService.shutdown();
    }

    /**
     * Lock required
     */
    private void submitBackgroundTask() {
        if (size() == 1) {
            this.executorService.submit(onTimePullThreadWork);
        }
    }

    /**
     * Lock required
     */
    private void tryForcePull() {
        if (size() == MAX_QUEUE_SIZE) {
            internalForcePull();
        }
    }

    /**
     * Lock required
     */
    private void internalForcePull() {
        T val = poll();
        onForceDequeCallback.call(val);
    }

    /**
     * Lock required
     */
    private void internalTimePull() {
        T val = poll();
        onTimeExpiredCallback.call(val);
    }

    /**
     * Lock required
     */
    private T poll() {
        if (size() == 0)
            return null;
        else
            return storage.remove(0).getValue();
    }

    /**
     * Lock required
     */
    private void sort() {
        Collections.sort(storage);
    }

    /**
     * Lock required
     */
    private void add(T value, Delay delay, long currentStamp) {
        storage.add(Stamped.stamped(value, delay.toMillis() + currentStamp));
    }

    private Stamped<T> peek() {
        return storage.get(0);
    }

    /**
     * Lock required
     */
    private int size() {
        return storage.size();
    }

    /**
     * Lock required
     *
     * @return millis for wait before next pull
     */
    private long onTimePullIteration() {
        if (size() > 0) {
            Stamped<T> nearest = peek();
            long       now     = Stamped.now();
            if (now - nearest.getStamp() >= 0) {
                internalTimePull();
                return 0;
            } else {
                return nearest.getStamp() - now;
            }
        }
        return -1;
    }

    /**
     * Background thread for pull
     */
    private Runnable onTimePullThreadWork = new Runnable() {
        @Override
        public void run() {
            while (true) {
                long millisBeforeNext;
                try {
                    lock.lock();
                    millisBeforeNext = DeferredQueue.this.onTimePullIteration();
                } finally {
                    lock.unlock();
                }
                if (millisBeforeNext == -1) {
                    return;
                }
                if (millisBeforeNext == 0) {
                    continue;
                }
                try {
                    Thread.sleep(millisBeforeNext);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
