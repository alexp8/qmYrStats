package org.cncnet.yrqm.model.enums;

import lombok.Getter;

@Getter
public enum YRFactionEnum {
    ALLIED("Allied"),
    SOVIET("Soviet"),
    YURI("Yuri");

    private final String name;

    YRFactionEnum(String name) {
        this.name = name;
    }
}