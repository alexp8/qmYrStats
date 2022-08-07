package org.cncnet.yrqm.model;

import lombok.Getter;

/**
 * Fields contained within a bulk.json QMReport json file.
 * The fields from a report. Only choosing fields I need for now
 */
@Getter
public class QMReport {
    private YRMap map;
    private String scen;
    private double duration;

    @Getter
    public static class YRMap {
        private String hash;
        private String id;
    }

    private QMPlayer[] players;

    @Getter
    public static class QMPlayer {
        private String name;
        private int disconnected;
        private int no_completion;
        private int won;  //1 = won, 0 = lost
        private int defeated;
        private int country;
        private int points;
        private String side;
    }
}