package org.cncnet.yrqm.model;

import lombok.Getter;
import lombok.Setter;

/**
 * A compiled report per map
 */
@Getter
@Setter
public abstract class YRCompiledReport implements Comparable<YRCompiledReport> {
    protected String mapName;

    public YRCompiledReport(String mapName) {
        this.mapName = mapName;
    }

    public abstract String getWinsLosses();

    public abstract int getTotalGames();

    @Override
    public int compareTo(YRCompiledReport o) {
        if (mapName.contains("Standard") && o.mapName.contains("Standard")) {
            return mapName.compareTo(o.mapName);
        } else if (mapName.contains("Standard")) {
            return -1;
        } else if (o.mapName.contains("Standard")) {
            return 1;
        } else {
            return mapName.compareTo(o.mapName);
        }
    }
}