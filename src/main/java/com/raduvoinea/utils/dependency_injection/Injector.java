package com.raduvoinea.utils.dependency_injection;

import com.raduvoinea.utils.dependency_injection.annotations.Inject;
import com.raduvoinea.utils.dependency_injection.exception.InjectionException;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.message_builder.MessageBuilder;
import com.raduvoinea.utils.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

// TODO Add tests
public class Injector {

    private final HashMap<Class<?>, Object> dependencies = new HashMap<>();

    public <Child extends Parent, Parent> Child bind(Class<Parent> clazz, Child object) {
        dependencies.put(clazz, object);
        return object;
    }

    public <T> T create(Class<T> clazz) throws InjectionException {
        T instance = null;

        for (Constructor<?> primitiveConstructor : clazz.getConstructors()) {
            //noinspection unchecked
            Constructor<T> constructor = (Constructor<T>) primitiveConstructor;
            instance = create(constructor);

            if (instance != null) {
                break;
            }
        }

        if (instance == null) {
            throw new InjectionException("""
                    Failed to locate one of the bellow:
                    - Constructor with @Inject annotation
                    - No args constructor
                    """);
        }

        return instance;
    }

    private <T> T create(Constructor<T> constructor) throws InjectionException {
        Inject inject = constructor.getAnnotation(Inject.class);
        constructor.setAccessible(true);

        // Create with no args constructor
        // There is nothing to inject so we can skip the @Inject check
        if (constructor.getParameterCount() == 0) {
            try {
                return constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
                throw new InjectionException(
                        new MessageBuilder(" Failed to create instance of class {class}.")
                                .parse("class", constructor.getDeclaringClass().getName())
                                .parse(),
                        exception);
            }
        }

        if (inject == null) {
            return null;
        }

        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = dependencies.getOrDefault(parameterTypes[i], null);

            if (parameters[i] == null) {
                if (inject.createMissingChildren()) {
                    parameters[i] = create(parameterTypes[i]);
                } else {
                    throw new InjectionException(
                            new MessageBuilder("""
                                    Failed to create instance of class {class}
                                    Missing dependency: {dependency}
                                    """)
                                    .parse("class", constructor.getDeclaringClass().getName())
                                    .parse("dependency", parameterTypes[i].getName())
                                    .parse()
                    );
                }
            }
        }

        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new InjectionException(
                    new MessageBuilder(" Failed to create instance of class {class}.")
                            .parse("class", constructor.getDeclaringClass().getName())
                            .parse(),
                    exception);
        }
    }

    public void inject(Object object) throws InjectionException {
        for (Field field : Reflections.getFields(object.getClass())) {
            inject(object, field);
        }
    }

    private void inject(Object object, Field field) throws InjectionException {
        Inject inject = field.getAnnotation(Inject.class);

        if (inject == null) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.getClass().getName().equals("com.google.inject.Inject")) {
                    Logger.warn("Google Guice annotation found. You might want to check if the field is being injected properly.");
                    return;
                }
            }

            return;
        }

        Object value = dependencies.getOrDefault(field.getType(), null);

        if (value == null) {
            throw new InjectionException(
                    new MessageBuilder("""
                            Failed to inject dependency into field {field} from class {class}
                            Missing dependency: {dependency}
                            """)
                            .parse("class", object.getClass().getName())
                            .parse("field", field.getName())
                            .parse("dependency", field.getType().getName())
                            .parse()
            );
        }

        try {
            Logger.debug("Injecting dependency: " + field.getType().getName());
            field.set(object, dependencies.get(field.getType()));
        } catch (IllegalAccessException exception) {
            throw new InjectionException(
                    new MessageBuilder("Failed to inject dependency {dependency} into the field {field}")
                            .parse("field", field.getName())
                            .parse("dependency", field.getType().getName())
                            .parse(),
                    exception
            );
        }
    }

}

/*


IBalance balance = IBalance.getByOwner(owner);
print(balance.getBalance());

 */
