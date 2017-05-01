package deferred_queue.core;

import java.util.concurrent.TimeUnit;

/**
 * Class represents waiting duration for pulled from queue
 */
public class Delay {
    private TimeUnit unit;
    private long     duration;

    private Delay(long duration, TimeUnit unit) {
        this.unit = unit;
        this.duration = duration;
    }

    public long toMillis() {
        return TimeUnit.MILLISECONDS.convert(duration, unit);
    }

    public static Delay delay(long duration, TimeUnit unit) {
        return new Delay(duration, unit);
    }
}
