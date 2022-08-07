package org.cncnet.yrqm.model.enums;

import lombok.Getter;

@Getter
public enum YRCountryEnum {
    America("America"),
    Great_Britain("Great_Britain"),
    Korea("Korea"),
    France("France"),
    Germany("Germany"),
    Iraq("Iraq"),
    Cuba("Cuba"),
    Russia("Russia"),
    Libya("Libya"),
    Yuri("Yuri");

    private final String name;

    YRCountryEnum(String name) {
        this.name = name;
    }
}