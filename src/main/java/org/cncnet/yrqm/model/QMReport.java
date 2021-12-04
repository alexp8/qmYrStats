package org.cncnet.yrqm.model;

import lombok.Getter;

/**
 * Fields contained within a bulk.json QMReport json file.
 * Only retrieving YRReports for now.
 */
@Getter
public class QMReport {

    private YRReport[] yr;

    /**
     * The fields from a YR report. Only choosing fields I need for now
     */
    @Getter
    public static class YRReport {
        private YRMap map;
        private String scen;

        @Getter
        public static class YRMap {
            private String hash;
            private String id;
        }

        private YRPlayer[] players;

        @Getter
        public static class YRPlayer {
            private String name;
            private int disconnected;
            private int no_completion;
            private int won;
            private int defeated;
            private int country;
            private int points;
            private String side;
        }
    }
}