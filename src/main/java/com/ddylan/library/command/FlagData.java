package com.ddylan.library.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.beans.ConstructorProperties;
import java.util.List;

@Getter
@AllArgsConstructor(onConstructor=@__({@ConstructorProperties({"names", "description", "defaultValue", "methodIndex"})}))
public class FlagData implements Data {

    private final List<String> names;
    private final String description;
    private final boolean defaultValue;
    private final int methodIndex;

}
