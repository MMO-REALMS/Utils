package com.raduvoinea.utils.file_manager.dto.gson;

import com.google.gson.*;
import com.raduvoinea.utils.file_manager.dto.serializable.ISerializable;
import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.reflections.Reflections;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;


public class InterfaceGsonTypeAdapter<T extends ISerializable> implements JsonDeserializer<T> {

    private static final String CLASS_NAME_FIELD = "className";

    protected final ClassLoader classLoader;
    private final @Getter Class<T> serializedClass;

    protected InterfaceGsonTypeAdapter(ClassLoader classLoader, Class<T> serializedClass) {
        this.serializedClass = serializedClass;
        this.classLoader = classLoader;
    }

    @SafeVarargs
    public static List<InterfaceGsonTypeAdapter<? extends ISerializable>> generate(ClassLoader classLoader, Class<? extends ISerializable>... classes) {
        return generate(classLoader, Arrays.asList(classes));
    }

    public static List<InterfaceGsonTypeAdapter<? extends ISerializable>> generate(ClassLoader classLoader, Reflections.Crawler reflections) {
        Set<Class<? extends ISerializable>> classes = reflections.getOfType(ISerializable.class);

        return generate(classLoader, classes);
    }

    public static List<InterfaceGsonTypeAdapter<? extends ISerializable>> generate(ClassLoader classLoader, Collection<Class<? extends ISerializable>> classes) {
        return new ArrayList<>(
                classes.stream()
                        .filter(clazz -> !clazz.isInterface())
                        .map(clazz -> new InterfaceGsonTypeAdapter<>(classLoader, clazz))
                        .toList()
        );
    }

    @SneakyThrows(value = {ClassNotFoundException.class})
    @Override
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        ISerializable output;

        if (!json.getAsJsonObject().has(CLASS_NAME_FIELD)) {
            Logger.error("Field class_name was not not found in json. Please check the definition of " +
                    type.getTypeName() + " and make sure it has the field class_name.");
            Logger.error("Json:");
            Logger.error(json.toString());
            return null;
        }

        String className = json.getAsJsonObject().get(CLASS_NAME_FIELD).getAsString();

        //noinspection unchecked
        Class<ISerializable> clazz = (Class<ISerializable>) classLoader.loadClass(className);

        output = context.deserialize(json, clazz);

        //noinspection unchecked
        return (T) output;
    }

    public void register(@NotNull GsonBuilder builder) {
        builder.registerTypeAdapter(this.getSerializedClass(), this);
    }
}
