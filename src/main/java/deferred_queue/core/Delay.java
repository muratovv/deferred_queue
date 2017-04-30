package deferred_queue.core;

import java.util.concurrent.TimeUnit;

/**
 * Class represents waiting duration for pulled from queue
 */
public class Delay {
    private TimeUnit unit;
    private long     duration;

    private Delay(TimeUnit unit, long duration) {
        this.unit = unit;
        this.duration = duration;
    }

    public long toMillis() {
        return TimeUnit.MILLISECONDS.convert(duration, unit);
    }

    public static Delay delay(TimeUnit unit, long duration) {
        return new Delay(unit, duration);
    }
}
