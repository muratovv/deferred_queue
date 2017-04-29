package deferred_queue.core;

/**
 * @author @muratovv
 * @date 30.04.17
 */
public class EmptyCallback<T> implements Callback<T> {
    @Override
    public void call(T value) {
    }
}
