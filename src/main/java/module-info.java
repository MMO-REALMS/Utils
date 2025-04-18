module com.raduvoinea.utils {
	exports com.raduvoinea.utils.event_manager;
	exports com.raduvoinea.utils.event_manager.dto;
	exports com.raduvoinea.utils.file_manager;
	exports com.raduvoinea.utils.lambda;
	exports com.raduvoinea.utils.lambda.lambda;
	exports com.raduvoinea.utils.logger;
	exports com.raduvoinea.utils.logger.dto;
	exports com.raduvoinea.utils.message_builder;
	exports com.raduvoinea.utils.reflections;
	exports com.raduvoinea.utils.generic;
	exports com.raduvoinea.utils.generic.dto;

	requires static lombok;
	requires static org.jetbrains.annotations;
	requires static com.google.gson;
	requires static redis.clients.jedis;
	requires static org.apache.commons.pool2;
	requires java.sql;
	requires java.desktop;
}