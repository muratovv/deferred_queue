package deferred_queue.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author @muratovv
 * @date 30.04.17
 */
public class DeferredQueueTest {

    private DeferredQueue<Integer> queue = new DeferredQueue<>();

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
        queue.insert(100500, null);
        queue.insert(666, null);
        assertEquals(true, callbackExecuted[0]);
    }
}