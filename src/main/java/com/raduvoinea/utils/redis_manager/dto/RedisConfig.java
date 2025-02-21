package com.raduvoinea.utils.redis_manager.dto;

import com.raduvoinea.utils.redis_manager.utils.NetworkUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisConfig {

    private String host = "127.0.0.1";
    private int port = 6379;
    private String password = "password";
    private String channelBase = "channel";

    // Advanced settings
    private String redisID = NetworkUtils.getHostname();
    private int timeout = 2000; // 2s
    private int waitBeforeIteration = 50; // 50ms

    public String getChannel() {
        return channelBase + "#" + redisID;
    }
}



