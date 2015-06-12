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
    private long fetchInterval = 59000;
    private String username = null;
    private String password = null;

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

            if (config.hasPath("fetch_interval")) {
                fetchInterval = config.getLong("fetch_interval");
            }

            if (config.hasPath("username")) {
                username = config.getString("username");
            }

            if (config.hasPath("password")) {
                password = config.getString("password");
            }
        }
    }

    public int getRiemannPort() {
        return riemannPort;
    }
    public String getRiemannHost() {
        return riemannHost;
    }
    public long getFetchInterval() { return fetchInterval; }
    public String getPassword() { return password; }
    public String getUsername() { return username; }
}
