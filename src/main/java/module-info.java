module com.voinearadu.utils {
    exports com.voinearadu.utils.event_manager;
    exports com.voinearadu.utils.file_manager;
    exports com.voinearadu.utils.lambda;
    exports com.voinearadu.utils.lambda.lambda;
    exports com.voinearadu.utils.logger;
    exports com.voinearadu.utils.logger.dto;
    exports com.voinearadu.utils.message_builder;
    exports com.voinearadu.utils.redis_manager;
    exports com.voinearadu.utils.reflections;
    exports com.voinearadu.utils.generic;
    exports com.voinearadu.utils.generic.dto;

    requires static lombok;
    requires static org.jetbrains.annotations;
    requires static com.google.gson;
    requires static redis.clients.jedis;
    requires static org.apache.commons.pool2;
    requires java.sql;
    requires java.desktop;
    requires com.google.guice;
}