package deferred_queue.core;

/**
 * Callback for using in {@link DeferredQueue}
 */
public interface Callback<T> {
    void call(T value);
}
