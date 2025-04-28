package com.raduvoinea.utils.file_manager.dto.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class InheritableDeserializerModule extends SimpleModule {

    private final JsonDeserializer<?> delegateDeserializer;
    private final Class<?> superClass;

    public InheritableDeserializerModule(Class<?> superClass, JsonDeserializer<?> delegateDeserializer) {
        this.superClass = superClass;
        this.delegateDeserializer = delegateDeserializer;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addDeserializers(new Deserializers.Base() {
            @Override
            public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) {
                if (superClass.isAssignableFrom(type.getRawClass())) {
                    return delegateDeserializer;
                }
                return null;
            }
        });
    }
}
