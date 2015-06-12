package com.rsr5.gocd.riemann_notifier;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

public class RetrievePipelineRSS {

    PluginConfig pluginConfig = null;
    String username = null;
    String password = null;

    public RetrievePipelineRSS() {
        pluginConfig = new PluginConfig();
        username = pluginConfig.getUsername();
        password = pluginConfig.getPassword();
    }

    public HttpURLConnection download() throws IOException {
        String sURL = "http://localhost:8153/go/cctray.xml";

        if (username != null && password != null){
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username,
                            password.toCharArray());
                }
            });
        }

        URL url = new URL(sURL);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();
        return request;
    }
}
