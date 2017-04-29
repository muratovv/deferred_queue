package deferred_queue.core;

import java.util.concurrent.TimeUnit;

/**
 * Class represents waiting time for pulled from queue
 */
public class Delay {
    private TimeUnit unit;
    private long     time;

    private Delay(TimeUnit unit, long time) {
        this.unit = unit;
        this.time = time;
    }

    public static Delay make(TimeUnit unit, long time) {
        return new Delay(unit, time);
    }
}
