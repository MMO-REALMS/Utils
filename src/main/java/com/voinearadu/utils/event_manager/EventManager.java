package com.voinearadu.utils.event_manager;

import com.google.inject.Injector;
import com.voinearadu.utils.event_manager.annotation.EventHandler;
import com.voinearadu.utils.event_manager.dto.EventMethod;
import com.voinearadu.utils.event_manager.dto.IEvent;
import com.voinearadu.utils.generic.dto.Holder;
import com.voinearadu.utils.logger.Logger;
import com.voinearadu.utils.message_builder.MessageBuilder;
import com.voinearadu.utils.reflections.Reflections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManager {

    private final HashMap<Class<?>, List<EventMethod>> methods = new HashMap<>();
    private final Holder<Injector> injectorHolder;

    private ExternalRegistrar externalRegistrar = new ExternalRegistrar() {
        @Override
        public boolean register(Object object, Method method, Class<?> eventClass) {
            Logger.warn("No external registrar found");
            return false;
        }

        @Override
        public boolean unregister(Method method, Class<?> eventClass) {
            Logger.warn("No external registrar found");
            return false;
        }
    };

    public EventManager() {
        this(Holder.empty());
    }

    public EventManager(Holder<Injector> injectorHolder) {
        this.injectorHolder = injectorHolder;
    }

    /**
     * Used to register an external registrar, that can catch any event that does not implement {@link IEvent}
     */
    public interface ExternalRegistrar {
        /**
         * @param object     The parent object where the event method is located
         * @param method     The actual event method
         * @param eventClass The event class
         * @return true if the method was registered successfully, false otherwise
         */
        boolean register(Object object, Method method, Class<?> eventClass);

        /**
         * @param method     The actual event method
         * @param eventClass The event class
         * @return true if the method was unregistered successfully, false otherwise
         */
        boolean unregister(Method method, Class<?> eventClass);
    }

    /**
     * Used to register an external registrar, that can catch any event that does not implement {@link IEvent}
     *
     * @param externalRegistrar the external registrar
     */
    public void registerExternalRegistrar(ExternalRegistrar externalRegistrar) {
        Logger.good("[EventManager] Registered external registrar");
        Logger.debug(this);
        this.externalRegistrar = externalRegistrar;
    }

    public @Nullable Object createObject(Class<?> clazz) {
        try {
            Constructor<?> constructor = null;

            if (clazz.getDeclaredConstructors().length != 0) {
                constructor = clazz.getConstructor();
            } else if (clazz.getConstructors().length != 0) {
                constructor = clazz.getConstructor();
            }

            if (constructor == null) {
                Logger.error("No constructors found for class " + clazz.getName());
                return null;
            }

            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException error) {
            Logger.error("Failed to register class " + clazz.getName() + ". Was unable to find a no-args constructor");
            Logger.error(error);
        }

        return null;
    }

    public void register(Class<?> clazz) {
        Object object = createObject(clazz);
        if (object == null) {
            return;
        }
        register(object);
    }

    public void register(@NotNull Object object) {
        Logger.debug(Reflections.getMethods(object.getClass()));
        for (Method method : Reflections.getMethods(object.getClass())) {
            register(object, method);
        }

        sortMethods();
    }

    public void unregister(@NotNull Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            unregister(method);
        }
    }

    public void fire(@NotNull Object event) {
        this.fire(event, true);
    }

    public void fire(@NotNull Object event, boolean suppressExceptions) {
        Class<?> eventClass = event.getClass();

        if (!methods.containsKey(eventClass)) {
            Logger.warn(new MessageBuilder("No listeners found for event {event}")
                    .parse("event", eventClass.getSimpleName())
            );
            return;
        }

        List<EventMethod> eventMethods = methods.get(eventClass);

        if (eventMethods.isEmpty()) {
            Logger.warn(new MessageBuilder("No listeners found for event {event}")
                    .parse("event", eventClass.getSimpleName())
            );
            return;
        }

        for (EventMethod method : eventMethods) {
            method.fire(event, suppressExceptions);
        }
    }

    protected Class<?> getEventClass(@NotNull Method method) {
        if (!method.isAnnotationPresent(EventHandler.class)) {
            Logger.debug(new MessageBuilder("Method {class}#{method} does not have the EventHandler annotation")
                    .parse("class", method.getDeclaringClass())
                    .parse("method", method.getName())
            );
            return null;
        }

        EventHandler annotation = method.getAnnotation(EventHandler.class);

        if (annotation.ignore()) {
            Logger.debug(new MessageBuilder("Ignoring method {class}#{method}")
                    .parse("class", method.getDeclaringClass())
                    .parse("method", method.getName())
            );
            return null;
        }

        if (method.getParameterCount() != 1) {
            Logger.error(new MessageBuilder("Method {method} from class {class} has {count} parameters, expected 1")
                    .parse("method", method.getName())
                    .parse("class", method.getDeclaringClass())
                    .parse("count", method.getParameterCount())
            );
            return null;
        }

        return method.getParameterTypes()[0];
    }

    private void register(Object parentObject, Method method) {
        Class<?> eventClass = getEventClass(method);

        if (eventClass == null) {
            return;
        }

        if (!injectorHolder.isEmpty()) {
            injectorHolder.value().injectMembers(parentObject);
        }

        Logger.debug(new MessageBuilder("Registering listener {listener} -> {class}#{method}")
                .parse("listener", eventClass.getSimpleName())
                .parse("class", method.getDeclaringClass())
                .parse("method", method.getName())
        );

        if (!IEvent.class.isAssignableFrom(eventClass)) {
            boolean result = externalRegistrar.register(parentObject, method, eventClass);

            if (!result) {
                Logger.error("Failed to register method " + method.getName() + " from class " +
                        method.getDeclaringClass() + " with event class " + eventClass.getName());
                Logger.debug(this);
            }

            return;
        }

        List<EventMethod> eventMethods = methods.getOrDefault(eventClass, new ArrayList<>());
        eventMethods.add(new EventMethod(parentObject, method));
        methods.put(eventClass, eventMethods);
    }

    private void unregister(Method method) {
        Class<?> eventClass = getEventClass(method);

        if (eventClass == null) {
            return;
        }

        if (!IEvent.class.isAssignableFrom(eventClass)) {
            boolean result = externalRegistrar.unregister(method, eventClass);

            if (!result) {
                Logger.error("Failed to unregister method " + method.getName() + " from class " +
                        method.getDeclaringClass() + " with event class " + eventClass.getName());
            }

            return;
        }

        List<EventMethod> eventMethods = methods.getOrDefault(eventClass, new ArrayList<>());
        boolean result = eventMethods.removeIf(eventMethod -> eventMethod.getMethod().equals(method));

        if (!result) {
            Logger.error("Failed to unregister method " + method.getName() + " from class " +
                    method.getDeclaringClass() + " with event class " + eventClass.getName());
            return;
        }

        methods.put(eventClass, eventMethods);
        Logger.warn("Unregistered method " + method.getName() + " from class " + method.getDeclaringClass() +
                " with event class " + eventClass.getName());
    }

    private void sortMethods() {
        for (Class<?> eventClass : methods.keySet()) {
            List<EventMethod> eventMethods = methods.getOrDefault(eventClass, new ArrayList<>());
            eventMethods.sort(new EventMethod.Comparator());
            methods.put(eventClass, eventMethods);
        }
    }


}
