package com.rsr5.gocd.riemann_notifier;

import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

public class TestPipelineDetailsPopulator {

    private String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    @Test
    public void test_pipeline_details_populator() {
        HttpURLConnection requestHistory = mock(HttpURLConnection.class);
        HttpURLConnection requestArtifacts = mock(HttpURLConnection.class);

        RetrievePipelineHistory retrieveHistory = mock(RetrievePipelineHistory
                .class);
        RetrieveStageArtifacts retrieveStageArtifacts = mock
                (RetrieveStageArtifacts.class);

        PipelineDetailsPopulator pipelineDetailsPopulator = new
                PipelineDetailsPopulator();
        pipelineDetailsPopulator.retrievePipelineInstance = retrieveHistory;
        pipelineDetailsPopulator.retrievePipelineArtifacts =
                retrieveStageArtifacts;

        String contentHistory = "{}";
        String contentArtifacts = "{}";
        String requestBody = "{}";
        try {
            requestBody = this.readFile("src/test/example_notification.json");
        } catch (IOException e) {
            System.out.println("can't load file example_notification.json");
        }

        try {
            contentHistory = this.readFile("src/test/test_content.json");
        } catch (IOException e) {
            System.out.println("can't load file test_content.json");
        }

        try {
            contentArtifacts = this.readFile("src/test/test_contents_artifacts.json");
        } catch (IOException e) {
            System.out.println("can't load file test_contents_artifacts.json");
        }

        try {
            when(requestHistory.getContent()).thenReturn(new ByteArrayInputStream
                    (contentHistory.getBytes("UTF-8")));
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        try {
            when(requestArtifacts.getContent()).thenReturn(new ByteArrayInputStream
                    (contentArtifacts.getBytes("UTF-8")));
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        try {
            when(retrieveHistory.download("pipeline1")).thenReturn(requestHistory);
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        try {
            when(retrieveStageArtifacts.download("pipeline1", "stage1", "1", "1", "job1")).thenReturn(requestHistory);
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        JsonObject json = pipelineDetailsPopulator.extendMessage(requestBody);

        assert (json.has("x-pipeline-instance-details"));
        assert (json.has("x-pipeline-artifacts"));
    }

    @Test
    public void test_pipeline_details_error() {
        HttpURLConnection request = mock(HttpURLConnection.class);
        RetrievePipelineHistory retrieve = mock(RetrievePipelineHistory
                .class);
        PipelineDetailsPopulator pipelineDetailsPopulator = new
                PipelineDetailsPopulator();
        pipelineDetailsPopulator.retrievePipelineInstance = retrieve;

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

        try {
            when(request.getContent()).thenThrow(new IOException());
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        try {
            when(retrieve.download("pipeline1")).thenReturn(request);
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        JsonObject json = pipelineDetailsPopulator.extendMessage(requestBody);

        assert (json.has("x-pipeline-error"));

    }
}
