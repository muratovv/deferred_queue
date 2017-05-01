package deferred_queue.core;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;
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

        queue.setOnForceDequeCallback(new Callback<Integer>() {
            @Override
            public void call(Integer value) {
                callbackExecuted[0] = true;
                assertEquals(100500, ((long) value));
            }
        });
        queue.insert(100500, delay(1, TimeUnit.HOURS));
        queue.insert(666, delay(1, TimeUnit.HOURS));
        assertEquals(true, callbackExecuted[0]);
    }

    @Test
    public void forceOnTimePullTest() throws Exception {
        final boolean[] callbackExecuted = {false};

        queue.setOnTimeExpiredCallback(new Callback<Integer>() {
            @Override
            public void call(Integer value) {
                callbackExecuted[0] = true;
                assertEquals(100500, ((long) value));
            }
        });
        queue.insert(100500, delay(1, TimeUnit.HOURS));
        queue.forceTimePull();
        assertEquals(true, callbackExecuted[0]);
    }

    @Test
    public void forcePullTest() throws Exception {
        final boolean[] callbackExecuted = {false};

        queue.setOnForceDequeCallback(new Callback<Integer>() {
            @Override
            public void call(Integer value) {
                callbackExecuted[0] = true;
                assertEquals(100500, ((long) value));
            }
        });
        queue.insert(100500, delay(1, TimeUnit.HOURS));
        queue.forcePull();
        assertEquals(true, callbackExecuted[0]);
    }

    @Test
    public void onTimeOneElementPullTest() throws Exception {
        final boolean[] callbackExecuted = {false};

        queue.setOnTimeExpiredCallback(new Callback<Integer>() {
            @Override
            public void call(Integer value) {
                callbackExecuted[0] = true;
                assertEquals(100500, ((long) value));
            }
        });
        queue.insert(100500, delay(1, TimeUnit.SECONDS));
        threadWait(delay(2, TimeUnit.SECONDS));
        assertEquals(true, callbackExecuted[0]);
    }

    @Test
    public void twoTaskSubmittedWithDelayTest() throws Exception {
        final int[] callbackExecuted = {0};

        queue.setOnTimeExpiredCallback(new Callback<Integer>() {
            @Override
            public void call(Integer value) {
                if (callbackExecuted[0] == 0) {
                    assertEquals(666, ((long) value));
                }
                if (callbackExecuted[0] == 1) {
                    assertEquals(777, ((long) value));
                }
                callbackExecuted[0] += 1;

            }
        });
        queue.insert(666, delay(500, TimeUnit.MILLISECONDS));
        threadWait(delay(2, TimeUnit.SECONDS));
        queue.insert(777, delay(500, TimeUnit.MILLISECONDS));
        threadWait(delay(2, TimeUnit.SECONDS));
        assertEquals(2, callbackExecuted[0]);
    }

    @Test
    public void twoTaskSortedTest() throws Exception {
        queue = new DeferredQueue<>(2, Executors.newSingleThreadExecutor());

        final int[] callbackExecuted = {0};

        queue.setOnTimeExpiredCallback(new Callback<Integer>() {
            @Override
            public void call(Integer value) {
                if (callbackExecuted[0] == 0) {
                    assertEquals(777, ((long) value));
                }
                if (callbackExecuted[0] == 1) {
                    assertEquals(666, ((long) value));
                }
                callbackExecuted[0] += 1;

            }
        });
        queue.insert(666, delay(500, TimeUnit.MILLISECONDS));
        // second insert must appear earlier then first
        queue.insert(777, delay(100, TimeUnit.MILLISECONDS));
        threadWait(delay(2, TimeUnit.SECONDS));
        assertEquals(2, callbackExecuted[0]);

    }

    private void threadWait(Delay delay) {
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}