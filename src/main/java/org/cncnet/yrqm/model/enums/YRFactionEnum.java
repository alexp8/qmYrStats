package org.cncnet.yrqm.model.enums;

import lombok.Getter;

@Getter
public enum YRFactionEnum {
    ALLIED("Allied"),
    SOVIET("Soviet"),
    YURI("Yuri");

    private String name;

    private YRFactionEnum(String name) {
        this.name = name;
    }
}