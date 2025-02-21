package com.raduvoinea.utils.redis_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raduvoinea.utils.event_manager.EventManager;
import com.raduvoinea.utils.file_manager.FileManager;
import com.raduvoinea.utils.generic.dto.Holder;
import com.raduvoinea.utils.message_builder.MessageBuilderManager;
import com.raduvoinea.utils.redis_manager.dto.RedisConfig;
import com.raduvoinea.utils.redis_manager.manager.DebugRedisManager;

import java.util.List;

public class Main {

    public Main() {
        Holder<Gson> gsonHolder = Holder.empty();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        gsonHolder.set(gson);

        FileManager fileManager = new FileManager(gsonHolder, "config");
        MessageBuilderManager.init(true);

        RedisConfig config = fileManager.load(RedisConfig.class, "");

        new DebugRedisManager(gsonHolder, config, getClass().getClassLoader(),
                new EventManager(), true, false, List.of(
                "dev#*"
        )
        );
    }

}
