package deferred_queue.core;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class provide queue with time deferred behavior
 */
public class DeferredQueue<T> {
    private int queueSize = 1;

    private Callback<T> onTimeDequeCallback  = new EmptyCallback<>();
    private Callback<T> onForceDequeCallback = new EmptyCallback<>();

    private Queue<T> internalQueue;

    public DeferredQueue(int capacity) {
        this.queueSize = capacity;
        internalQueue = new LinkedBlockingQueue<>(capacity);
    }

    public DeferredQueue() {
        this(1);
    }

    public void updateOnTimeDequeCallback(Callback<T> onTimeDequeCallback) {
        this.onTimeDequeCallback = onTimeDequeCallback;
    }

    public void updateOnForceDequeCallback(Callback<T> onForceDequeCallback) {
        this.onForceDequeCallback = onForceDequeCallback;
    }

    /**
     * Insert {@code value} to queue
     *
     * @param value target object
     * @param delay after it, {@value} will be polled and push in {@link DeferredQueue#onTimeDequeCallback}
     */
    public void insert(T value, Delay delay) {
        if (internalQueue.size() == queueSize) {
            forcePull();
        }
        internalQueue.add(value);
    }

    /**
     * Force deque element with call {@link DeferredQueue#onTimeDequeCallback}
     */
    public void forceOnTimePull() {
        OnTimeDeque();
    }

    /**
     * Force deque element with call {@link DeferredQueue#onForceDequeCallback}
     */
    public void forcePull() {
        T val = internalQueue.poll();
        onForceDequeCallback.call(val);
    }

    private void OnTimeDeque() {
        T val = internalQueue.poll();
        onTimeDequeCallback.call(val);
    }

}
