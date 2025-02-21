package com.raduvoinea.event_manager;

import com.raduvoinea.event_manager.dto.*;
import com.raduvoinea.event_manager.manager.TestEventListener;
import com.raduvoinea.event_manager.manager.TestUnregisterEventListener;
import com.raduvoinea.utils.event_manager.EventManager;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventManagerTests {

    @Getter
    private static final EventManager eventManager = new EventManager();

    @BeforeAll
    public static void setup() {
        eventManager.registerExternalRegistrar(new EventManager.ExternalRegistrar() {
            @Override
            public boolean register(Object object, Method method, Class<?> eventClass) {
                if (object instanceof ExternalEvent externalEvent) {
                    eventManager.register(new ExternalEvent.Wrapper(externalEvent));
                    return true;
                }
                return false;
            }

            @Override
            public boolean unregister(Method method, Class<?> eventClass) {
                // Not supported
                return false;
            }
        });
        eventManager.register(new TestEventListener());
    }

    @Test
    public void testEvent() {
        TestEvent event1 = new TestEvent(1, 2);
        TestEvent event2 = new TestEvent(10, 20);

        eventManager.fire(event1);
        eventManager.fire(event2);

        assertEquals(3, event1.getResult());
        assertEquals(30, event2.getResult());
    }

    @Test
    public void testLocalEvent() {
        TestLocalEvent event1 = new TestLocalEvent(1, 2);
        TestLocalEvent event2 = new TestLocalEvent(10, 20);

        // We are not checking for the result as it has not been set by default

        event1.fire();
        event2.fire();

        assertEquals(3, event1.getResult());
        assertEquals(30, event2.getResult());
    }

    @Test
    public void testLocalRequest() {
        TestLocalRequest event1 = new TestLocalRequest(1, 2);
        TestLocalRequest event2 = new TestLocalRequest(10, 20);

        assertEquals(0, event1.getResult());
        assertEquals(0, event2.getResult());

        eventManager.fire(event1);
        eventManager.fire(event2);

        assertEquals(3, event1.getResult());
        assertEquals(30, event2.getResult());
    }

    @Test
    public void testExternalEvent() {
        ExternalEvent event1 = new ExternalEvent(1, 2);
        ExternalEvent event2 = new ExternalEvent(10, 20);

        // We are not checking for the result as it has not been set by default

        eventManager.fire(event1);
        eventManager.fire(event2);

        assertEquals(3, event1.getResult());
        assertEquals(30, event2.getResult());
    }

    @Test
    public void testUnregisterEvent() {
        eventManager.register(TestUnregisterEventListener.class);

        TestUnregisterEvent event1 = new TestUnregisterEvent(1, 2);
        eventManager.fire(event1);

        eventManager.unregister(TestUnregisterEventListener.class);

        TestUnregisterEvent event2 = new TestUnregisterEvent(1, 2);
        eventManager.fire(event2);

        assertEquals(3, event1.getResult());
        assertEquals(0, event2.getResult());
    }


}
