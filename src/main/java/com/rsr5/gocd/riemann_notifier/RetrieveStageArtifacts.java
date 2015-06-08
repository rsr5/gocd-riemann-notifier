package com.rsr5.gocd.riemann_notifier;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RetrieveStageArtifacts {
    public HttpURLConnection download(String pipelineName, String stageName,
                                      int pipelineCounter, int stageCounter,
                                      String jobName) throws IOException {
        String sURL = "http://localhost:8153/go/files/" + pipelineName + "/"
                + pipelineCounter + "/" + stageName + "/" + stageCounter +
                "/" + jobName + ".json";
        URL url = new URL(sURL);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();
        return request;
    }
}
