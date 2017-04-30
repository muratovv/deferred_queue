package deferred_queue.core;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static deferred_queue.core.Delay.delay;
import static org.junit.Assert.assertEquals;

/**
 * Use cases and unit tests for {@link DeferredQueue}
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
        queue.forceTimePull();
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

    @Test
    public void onTimeOneElementPullTest() throws Exception {
        final boolean[] callbackExecuted = {false};

        queue.updateOnTimeDequeCallback(new Callback<Integer>() {
            @Override
            public void call(Integer value) {
                callbackExecuted[0] = true;
                assertEquals(100500, ((long) value));
            }
        });
        queue.insert(100500, delay(TimeUnit.SECONDS, 1));
        threadWait(delay(TimeUnit.SECONDS, 2));
        assertEquals(true, callbackExecuted[0]);
    }

    private void threadWait(Delay delay) {
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}