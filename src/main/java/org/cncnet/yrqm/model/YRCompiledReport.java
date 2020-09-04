package org.cncnet.yrqm.model;

import lombok.Getter;
import lombok.Setter;

/**
 * A compiled report per map
 */
@Getter
@Setter
public abstract class YRCompiledReport {
    protected String mapName;

    public YRCompiledReport(String mapName) {
        this.mapName = mapName;

    }

    public abstract String getWinsLosses();

    public abstract int getTotalGames();
}