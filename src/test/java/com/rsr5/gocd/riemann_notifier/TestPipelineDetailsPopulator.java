package com.rsr5.gocd.riemann_notifier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestPipelineDetailsPopulator {

    private String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    @Test
    public void test_pipeline_details_populator() {
        HttpURLConnection request = mock(HttpURLConnection.class);
        RetrievePipelineInstance retrieve = mock(RetrievePipelineInstance.class);
        PipelineDetailsPopulator pipelineDetailsPopulator = new PipelineDetailsPopulator();
        pipelineDetailsPopulator.retrievePipelineInstance = retrieve;

        String content = "{}";
        String requestBody = "{\"pipeline-name\": \"pipeline1\"}";
        try {
            content = this.readFile("src/test/test_content.json");
        } catch (IOException e) {
            System.out.println("can't load file test_content.json");
        }

        try {
            when(request.getContent()).thenReturn(new ByteArrayInputStream(content.getBytes("UTF-8")));
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        try {
            when(retrieve.download("pipeline1")).thenReturn(request);
        } catch (IOException e) {
            // This will not happen, because Mockito.
        }

        requestBody = pipelineDetailsPopulator.extendMessage(requestBody);

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(requestBody).getAsJsonObject();

        assert(json.has("x-pipeline-instance-details"));
    }

    @Test
    public void test_pipeline_details_error() {
        HttpURLConnection request = mock(HttpURLConnection.class);
        RetrievePipelineInstance retrieve = mock(RetrievePipelineInstance.class);
        PipelineDetailsPopulator pipelineDetailsPopulator = new PipelineDetailsPopulator();
        pipelineDetailsPopulator.retrievePipelineInstance = retrieve;

        String content = "{}";
        String requestBody = "{\"pipeline-name\": \"pipeline1\"}";
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

        requestBody = pipelineDetailsPopulator.extendMessage(requestBody);

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(requestBody).getAsJsonObject();

        assert(json.has("x-pipeline-error"));
    }
}
