package com.ddylan.library.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.beans.ConstructorProperties;
import java.util.Set;

@Getter
@AllArgsConstructor(onConstructor=@__({@ConstructorProperties({"name", "defaultValue", "type", "methodIndex", "tabCompleteFlags", "parameterType"})}))
public class ParameterData implements Data {

    private final String name;
    private final String defaultValue;
    private final Class<?> type;
    private final boolean wildcard;
    private final int methodIndex;
    private final Set<String> tabCompleteFlags;
    private final Class<? extends ParameterType> parameterType;

}
