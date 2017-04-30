package deferred_queue.core;

/**
 * Value with expireTimestamp
 */
public class Stamped<T> implements Comparable<Stamped<T>> {
    private T    value;
    private long expireTimestamp;

    private Stamped(T value, long expireTimestamp) {
        this.value = value;
        this.expireTimestamp = expireTimestamp;
    }

    public T get() {
        return value;
    }

    public static <T> Stamped<T> stamped(T value, long timestamp) {
        return new Stamped<>(value, timestamp);
    }

    @Override
    public int compareTo(Stamped<T> o) {
        return Long.compare(this.expireTimestamp, o.expireTimestamp);
    }

    public long now(){
        return System.currentTimeMillis();
    }
}
