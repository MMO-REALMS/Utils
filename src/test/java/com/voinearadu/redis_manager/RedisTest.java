package com.voinearadu.redis_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.voinearadu.redis_manager.dto.event_serialization.ComplexEvent1;
import com.voinearadu.redis_manager.dto.event_serialization.SimpleEvent1;
import com.voinearadu.redis_manager.dto.event_serialization.SimpleEvent2;
import com.voinearadu.redis_manager.manager.TestListener;
import com.voinearadu.utils.event_manager.EventManager;
import com.voinearadu.utils.file_manager.dto.gson.SerializableListGsonTypeAdapter;
import com.voinearadu.utils.file_manager.dto.gson.SerializableMapGsonTypeAdapter;
import com.voinearadu.utils.file_manager.dto.gson.SerializableObjectTypeAdapter;
import com.voinearadu.utils.generic.dto.Holder;
import com.voinearadu.utils.message_builder.MessageBuilderManager;
import com.voinearadu.utils.redis_manager.dto.RedisConfig;
import com.voinearadu.utils.redis_manager.dto.RedisResponse;
import com.voinearadu.utils.redis_manager.dto.gson.RedisRequestGsonTypeAdapter;
import com.voinearadu.utils.redis_manager.event.RedisRequest;
import com.voinearadu.utils.redis_manager.manager.RedisManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RedisTest {

    private static RedisManager redisManager;
    private static final Holder<Gson> gsonHolder = Holder.empty();


    @BeforeAll
    public static void init() {
        MessageBuilderManager.init(false);

        ClassLoader classLoader = RedisTest.class.getClassLoader();

        redisManager = new RedisManager(gsonHolder, new RedisConfig(), classLoader, new EventManager(), true, true);

        RedisRequestGsonTypeAdapter redisRequestTypeAdapter = new RedisRequestGsonTypeAdapter(classLoader, redisManager);
        SerializableListGsonTypeAdapter serializableListGsonTypeAdapter = new SerializableListGsonTypeAdapter(classLoader);
        SerializableMapGsonTypeAdapter serializableMapGsonTypeAdapter = new SerializableMapGsonTypeAdapter(classLoader);
        SerializableObjectTypeAdapter serializableObjectTypeAdapter = new SerializableObjectTypeAdapter(classLoader);


        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(redisRequestTypeAdapter.getSerializedClass(), redisRequestTypeAdapter)
                .registerTypeAdapter(serializableListGsonTypeAdapter.getSerializedClass(), serializableListGsonTypeAdapter)
                .registerTypeAdapter(serializableMapGsonTypeAdapter.getSerializedClass(), serializableMapGsonTypeAdapter)
                .registerTypeAdapter(serializableObjectTypeAdapter.getSerializedClass(), serializableObjectTypeAdapter);

        Gson gson = gsonBuilder.create();
        gsonHolder.set(gson);

        redisManager.getEventManager().register(TestListener.class);
    }

    @Test
    public void simpleEvent1() {
        SimpleEvent1 event1 = new SimpleEvent1(redisManager, 10, 20);
        RedisResponse<Integer> result = event1.sendAndWait();

        assertFalse(result.hasTimeout());
        assertTrue(result.isFinished());
        assertEquals(30, result.getResponse());
    }

    @Test
    public void simpleEvent2() {
        SimpleEvent2 event1 = new SimpleEvent2(redisManager, Arrays.asList("test1", "test2"), "-");
        RedisResponse<String> result = event1.sendAndWait();

        assertFalse(result.hasTimeout());
        assertTrue(result.isFinished());
        assertEquals("test1-test2-", result.getResponse());
    }

    @Test
    public void complexEvent1() {
        ComplexEvent1 event1 = new ComplexEvent1(redisManager, Arrays.asList("test1", "test2"), "test3");
        RedisResponse<List<String>> result = event1.sendAndWait();

        assertFalse(result.hasTimeout());
        assertTrue(result.isFinished());
        assertNotNull(result);
        assertEquals(3, result.getResponse().size());
        assertEquals("test1", result.getResponse().get(0));
        assertEquals("test2", result.getResponse().get(1));
        assertEquals("test3", result.getResponse().get(2));
    }

    @Test
    public void testGsonImplementation1() {
        RedisRequest<Boolean> event = new RedisRequest<>(redisManager, "test");
        event.setId(100);
        event.setOriginator("test_env");

        String json = redisManager.getGsonHolder().value().toJson(event);

        RedisRequest<?> event2 = RedisRequest.deserialize(redisManager, json);

        assertNotNull(event2);
        assertEquals(event.getClassName(), event2.getClassName());
        assertEquals(event.getId(), event2.getId());
        assertEquals(event.getOriginator(), event2.getOriginator());
        assertEquals(event.getTarget(), event2.getTarget());
    }

    @Test
    public void testGsonImplementation2() {
        ComplexEvent1 event = new ComplexEvent1(redisManager, Arrays.asList("test1", "test2"), "test3");
        event.setId(100);
        event.setOriginator("test_env");

        String json = redisManager.getGsonHolder().value().toJson(event);

        RedisRequest<?> event2 = RedisRequest.deserialize(redisManager, json);

        assertNotNull(event2);
        assertEquals(event.getClassName(), event2.getClassName());
        assertEquals(event.getId(), event2.getId());
        assertEquals(event.getOriginator(), event2.getOriginator());
        assertEquals(event.getTarget(), event2.getTarget());
        assertInstanceOf(ComplexEvent1.class, event2);

        ComplexEvent1 event3 = (ComplexEvent1) event2;

        assertNotNull(event3);
        assertEquals(event.getA(), event3.getA());
        assertEquals(event.getB(), event3.getB());
    }

}
