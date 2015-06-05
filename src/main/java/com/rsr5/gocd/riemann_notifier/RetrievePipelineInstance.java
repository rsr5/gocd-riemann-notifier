package com.rsr5.gocd.riemann_notifier;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RetrievePipelineInstance {
    public HttpURLConnection download(String pipelineName) throws IOException {
        String sURL = "http://localhost:8153/go/api/pipelines/" + pipelineName + "/history";
        URL url = new URL(sURL);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();
        return request;
    }
}
