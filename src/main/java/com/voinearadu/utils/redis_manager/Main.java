package com.voinearadu.utils.redis_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.voinearadu.utils.event_manager.EventManager;
import com.voinearadu.utils.file_manager.FileManager;
import com.voinearadu.utils.generic.dto.Holder;
import com.voinearadu.utils.message_builder.MessageBuilderManager;
import com.voinearadu.utils.redis_manager.dto.RedisConfig;
import com.voinearadu.utils.redis_manager.manager.DebugRedisManager;

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
                "kingdoms_core_dev#*"
        )
        );
    }

}
