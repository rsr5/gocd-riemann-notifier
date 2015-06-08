package com.rsr5.gocd.riemann_notifier;

import com.aphyr.riemann.Proto.Msg;
import com.aphyr.riemann.client.EventDSL;
import com.aphyr.riemann.client.IPromise;
import com.aphyr.riemann.client.RiemannClient;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

public class TestGoNotificationPlugin {
    @Test
    public void test_should_run() {
        assert (true);
    }

    private String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    @Test
    public void test_riemann_connect() {
        RiemannClient client = mock(RiemannClient.class);
        EventDSL eventDSL = mock(EventDSL.class);
        IPromise<Msg> msg = (IPromise<Msg>) mock(IPromise.class);
        HttpURLConnection request = mock(HttpURLConnection.class);
        RetrievePipelineHistory retrieve = mock(RetrievePipelineHistory
                .class);
        PipelineDetailsPopulator pipelineDetailsPopulator = new
                PipelineDetailsPopulator();
        pipelineDetailsPopulator.retrievePipelineInstance = retrieve;

        GoNotificationPlugin goNotificationPlugin = new GoNotificationPlugin();
        goNotificationPlugin.riemann = client;
        goNotificationPlugin.populator = pipelineDetailsPopulator;

        String content = "{}";
        String requestBody = "{}";
        try {
            requestBody = this.readFile("src/test/example_notification.json");
        } catch (IOException e) {
            System.out.println("can't load file example_notification.json");
        }

        try {
            content = this.readFile("src/test/test_content.json");
        } catch (IOException e) {
            System.out.println("can't load file test_content.json");
        }

        GoPluginApiRequest apiRequest = mock(GoPluginApiRequest.class);

        when(client.event()).thenReturn(eventDSL);
        when(eventDSL.service("gocd.group1.pipeline1.1.stage1.1")).thenReturn(eventDSL);
        when(eventDSL.description(anyString())).thenReturn(eventDSL);
        when(eventDSL.state("Passed")).thenReturn(eventDSL);
        when(eventDSL.send()).thenReturn(msg);
        try {
            when(msg.deref(5000, java.util.concurrent.TimeUnit.MILLISECONDS))
                    .thenReturn(null);
        } catch (IOException e) {
            // This won't happen, because Mockito.
        }


        try {
            when(request.getContent()).thenReturn(new ByteArrayInputStream
                    (content.getBytes("UTF-8")));
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        try {
            when(retrieve.download("pipeline1")).thenReturn(request);
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        when(apiRequest.requestBody()).thenReturn(requestBody);

        goNotificationPlugin.handleStageNotification(apiRequest);

        //verify(eventDSL, times(1)).service("fridge");
        //verify(eventDSL, times(1)).state("Info");
        //verify(eventDSL, times(1)).metric(5.3);
        //verify(eventDSL, times(1)).tags("appliance", "cold");
        //verify(eventDSL, times(1)).send();
    }
}
