package deferred_queue.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class provide queue with time deferred behavior
 */
@SuppressWarnings("WeakerAccess")
public class DeferredQueue<T> {
    private final int MAX_QUEUE_SIZE;

    private Callback<T> onTimeDequeCallback  = new EmptyCallback<>();
    private Callback<T> onForceDequeCallback = new EmptyCallback<>();

    private Lock lock;

    private ArrayList<Stamped<T>> storage;

    public DeferredQueue(int capacity) {
        this.MAX_QUEUE_SIZE = capacity;
        storage = new ArrayList<>(capacity);
        this.lock = new ReentrantLock(false);
    }

    public DeferredQueue() {
        this(1);
    }

    public void updateOnTimeDequeCallback(Callback<T> onTimeDequeCallback) {
        try {
            lock.lock();
            this.onTimeDequeCallback = onTimeDequeCallback;
        } finally {
            lock.unlock();
        }
    }

    public void updateOnForceDequeCallback(Callback<T> onForceDequeCallback) {
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
     * @param delay after it, {@value} will be polled and push in {@link DeferredQueue#onTimeDequeCallback}
     */
    public void insert(T value, Delay delay) {
        try {
            lock.lock();

            tryForcePull();
            add(value, delay, Stamped.now());
            sort();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Force deque element with call {@link DeferredQueue#onTimeDequeCallback}
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
     * Lock required
     */
    private void tryForcePull() {
        if (size() == MAX_QUEUE_SIZE) {
            internalForcePull();
        }
    }

    private void internalForcePull() {
        T val = poll();
        onForceDequeCallback.call(val);
    }

    /**
     * Lock required
     */
    private void internalTimePull() {
        T val = poll();
        onTimeDequeCallback.call(val);
    }

    /**
     * Lock required
     */
    private T poll() {
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
     * @return wait before next pull
     */
    private long OnTimePullIteration() {
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
        return 0;
    }
}
