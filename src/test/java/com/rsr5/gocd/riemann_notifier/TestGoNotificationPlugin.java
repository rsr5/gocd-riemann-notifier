package com.rsr5.gocd.riemann_notifier;

import com.aphyr.riemann.Proto.Msg;
import com.aphyr.riemann.client.EventDSL;
import com.aphyr.riemann.client.IPromise;
import com.aphyr.riemann.client.RiemannClient;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class TestGoNotificationPlugin {
    @Test
    public void test_should_run() {
        assert (true);
    }

    @Test
    public void test_riemann_connect() {
        RiemannClient client = mock(RiemannClient.class);
        EventDSL eventDSL = mock(EventDSL.class);
        IPromise<Msg> msg = (IPromise<Msg>) mock(IPromise.class);

        GoNotificationPlugin goNotificationPlugin = new GoNotificationPlugin();
        goNotificationPlugin.riemann = client;

        when(client.event()).thenReturn(eventDSL);
        when(eventDSL.service("fridge")).thenReturn(eventDSL);
        when(eventDSL.state("Info")).thenReturn(eventDSL);
        when(eventDSL.metric(5.3)).thenReturn(eventDSL);
        when(eventDSL.tags("appliance", "cold")).thenReturn(eventDSL);
        when(eventDSL.send()).thenReturn(msg);
        try {
            when(msg.deref(5000, java.util.concurrent.TimeUnit.MILLISECONDS))
                    .thenReturn(null);
        } catch (IOException e) {
            // This won't happen, because Mockito.
        }

        goNotificationPlugin.handleStageNotification(null);

        verify(eventDSL, times(1)).service("fridge");
        verify(eventDSL, times(1)).state("Info");
        verify(eventDSL, times(1)).metric(5.3);
        verify(eventDSL, times(1)).tags("appliance", "cold");
        verify(eventDSL, times(1)).send();
    }
}
