package com.rsr5.gocd.riemann_notifier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;

public class PipelineDetailsPopulator {

    protected RetrievePipelineHistory retrievePipelineInstance = new
            RetrievePipelineHistory();

    protected RetrieveStageArtifacts retrievePipelineArtifacts = new
            RetrieveStageArtifacts();

    private JsonObject mergeInPipelineInstanceDetails(String attributeName,
                                                      JsonElement notification,
                                                      JsonElement
                                                              pipelineInstance) {
        JsonObject json = notification.getAsJsonObject();
        json.add(attributeName, pipelineInstance);
        return json;
    }

    protected JsonElement downloadPipelineDetails(String pipelineName) throws
            IOException {


        HttpURLConnection request = retrievePipelineInstance.download
                (pipelineName);

        JsonParser parser = new JsonParser();
        JsonElement rootElement = parser.parse(new InputStreamReader(
                (InputStream) request.getContent()));
        JsonObject json = rootElement.getAsJsonObject();
        JsonArray pipelines = json.get("pipelines").getAsJsonArray();
        return pipelines.get(0);
    }

    protected JsonObject downloadPipelineArtifacts(String pipelineName,
                                                   String stageName,
                                                   String pipelineCounter,
                                                   String stageCounter,
                                                   String jobName) throws
            IOException {

        HttpURLConnection request = retrievePipelineArtifacts.download
                (pipelineName, stageName, pipelineCounter, stageCounter,
                        jobName);

        JsonObject json = new JsonObject();
        JsonParser parser = new JsonParser();
        JsonElement rootElement = parser.parse(new InputStreamReader(
                (InputStream) request.getContent()));

        json.add(jobName, rootElement.getAsJsonArray());

        return json;
    }

    public JsonObject extendMessage(String requestBody) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(requestBody).getAsJsonObject();

        try {
            JsonObject pipelineObject = (JsonObject) json.get("pipeline");
            JsonObject stageObject = (JsonObject) pipelineObject.get("stage");

            String pipeline = pipelineObject.get("name").getAsString();
            String pipelineCounter = pipelineObject.get("counter")
                    .getAsString();
            String stage = stageObject.get("name").getAsString();
            String stageCounter = stageObject.get("counter").getAsString();

            JsonElement extraDetails = downloadPipelineDetails(pipeline);
            json = mergeInPipelineInstanceDetails("x-pipeline-instance" +
                    "-details", json, extraDetails);

            json.add("x-pipeline-artifacts", new JsonObject());
            JsonArray jobArtifacts = new JsonArray();

            JsonArray jobs = stageObject.get("jobs").getAsJsonArray();
            for(final JsonElement job : jobs) {

                extraDetails = downloadPipelineArtifacts(pipeline, stage,
                        pipelineCounter, stageCounter, job.getAsJsonObject().get("name").getAsString());
                jobArtifacts.add(extraDetails);
            }

            json = mergeInPipelineInstanceDetails("x-pipeline-artifacts",
                    json, jobArtifacts);
        } catch (IOException e) {
            json.addProperty("x-pipeline-error", "Error connecting to GoCD " +
                    "API.");
        }
        
        return json;
    }
}
