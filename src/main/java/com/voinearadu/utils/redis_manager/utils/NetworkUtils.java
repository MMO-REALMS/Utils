package com.voinearadu.utils.redis_manager.utils;

import com.voinearadu.utils.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class NetworkUtils {

    public static @NotNull String getHostname() {
        String hostname;

        try {
            hostname = ShellUtils.execute("hostname --fqdn");
        } catch (IOException e) {
            hostname = System.getenv("HOSTNAME");

            if (hostname == null) {
                Logger.error("There was an error while trying to get the hostname. Environment variable HOSTNAME does not exist");
                throw new RuntimeException("Failed to get hostname");
            }
        }

        hostname = hostname.replace("\n", "").strip();

        return hostname;
    }

}
