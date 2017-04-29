package deferred_queue.core;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static deferred_queue.core.Delay.delay;
import static org.junit.Assert.assertEquals;

/**
 * @author @muratovv
 * @date 30.04.17
 */
public class DeferredQueueTest {

    private DeferredQueue<Integer> queue;

    @Before
    public void setUp() throws Exception {
        queue = new DeferredQueue<>();
    }

    @Test
    public void forceDequeTest() throws Exception {
        final boolean[] callbackExecuted = {false};

        queue.updateOnForceDequeCallback(new Callback<Integer>() {
            @Override
            public void call(Integer value) {
                callbackExecuted[0] = true;
                assertEquals(100500, ((long) value));
            }
        });
        queue.insert(100500, delay(TimeUnit.HOURS, 1));
        queue.insert(666, delay(TimeUnit.HOURS, 1));
        assertEquals(true, callbackExecuted[0]);
    }

    @Test
    public void forceOnTimePullTest() throws Exception {
        final boolean[] callbackExecuted = {false};

        queue.updateOnTimeDequeCallback(new Callback<Integer>() {
            @Override
            public void call(Integer value) {
                callbackExecuted[0] = true;
                assertEquals(100500, ((long) value));
            }
        });
        queue.insert(100500, delay(TimeUnit.HOURS, 1));
        queue.forceOnTimePull();
        assertEquals(true, callbackExecuted[0]);
    }

    @Test
    public void forcePullTest() throws Exception {
        final boolean[] callbackExecuted = {false};

        queue.updateOnForceDequeCallback(new Callback<Integer>() {
            @Override
            public void call(Integer value) {
                callbackExecuted[0] = true;
                assertEquals(100500, ((long) value));
            }
        });
        queue.insert(100500, delay(TimeUnit.HOURS, 1));
        queue.forcePull();
        assertEquals(true, callbackExecuted[0]);
    }
}