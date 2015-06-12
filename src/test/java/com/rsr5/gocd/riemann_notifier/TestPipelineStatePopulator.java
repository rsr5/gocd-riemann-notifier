package com.rsr5.gocd.riemann_notifier;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TestPipelineStatePopulator {

    private String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    @Test
    public void test_pipeline_state_populator() {
        HttpURLConnection request = mock(HttpURLConnection.class);
        RetrievePipelineRSS retrieve = mock(RetrievePipelineRSS.class);

        String content = "";

        try {
            content = this.readFile("src/test/rss.xml");
        } catch (IOException e) {
            System.out.println("can't load file rss.xml");
        }

        try {
            when(request.getContent()).thenReturn(new ByteArrayInputStream
                    (content.getBytes("UTF-8")));
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        try {
            when(retrieve.download()).thenReturn(request);
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        PipelineStatePopulator pipelineStatePopulator = new
                PipelineStatePopulator();
        pipelineStatePopulator.retrievePipelineRSS = retrieve;

        HashMap<String, String> stageStates = new HashMap<>();
        try {
            stageStates = pipelineStatePopulator.getStageStates();
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        assert(stageStates.containsKey("pipeline1:stage1"));
        assertEquals(stageStates.get("pipeline1:stage1"), "Sleeping");
    }
}
