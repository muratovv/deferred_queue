package deferred_queue.core;

import java.util.concurrent.TimeUnit;

/**
 * @author @muratovv
 * @date 30.04.17
 */
public class Delay {
    TimeUnit unit;
    long     time;

    private Delay(TimeUnit unit, long time) {
        this.unit = unit;
        this.time = time;
    }

    public static Delay make(TimeUnit unit, long time) {
        return new Delay(unit, time);
    }
}
