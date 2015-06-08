package com.rsr5.gocd.riemann_notifier;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class PluginConfig {
    private static Logger LOGGER = Logger.getLoggerFor(GoNotificationPlugin
            .class);
    private static final String PLUGIN_CONF = "gocd-riemann-notifier.conf";

    private int riemannPort = 5555;
    private String riemannHost = "localhost";


    public PluginConfig() {
        String userHome = System.getProperty("user.home");
        File pluginConfig = new File(userHome + File.separator + PLUGIN_CONF);
        if (!pluginConfig.exists()) {
            LOGGER.warn(String.format("Config file %s was not found in %s. " +
                    "Using default values.", PLUGIN_CONF, userHome));
        } else {
            Config config = ConfigFactory.parseFile(pluginConfig);
            if (config.hasPath("riemann_port")) {
                riemannPort = config.getInt("riemann_port");
            }

            if (config.hasPath("riemann_host")) {
                riemannHost = config.getString("riemann_host");
            }
        }
    }

    public int getRiemannPort() {
        return riemannPort;
    }

    public String getRiemannHost() {
        return riemannHost;
    }
}
