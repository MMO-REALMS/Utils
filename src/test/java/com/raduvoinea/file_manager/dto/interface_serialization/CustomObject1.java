package com.raduvoinea.file_manager.dto.interface_serialization;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomObject1 implements CustomInterface {

    @SuppressWarnings("unused")
    @SerializedName("class_name")
    private final String className = CustomObject1.class.getName();
    public String data;

}
