package org.cncnet.yrqm.model.enums;

import lombok.Getter;

@Getter
public enum QMGame {
    RA("ra"), RA2("ra2"), YR("yr"), TS("ts"), SFJ("sfj"), BLITZ("blitz");

    private final String game;

    QMGame(String game) {
        this.game = game;
    }
}