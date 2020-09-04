package org.cncnet.yrqm.model.reports;

import org.cncnet.yrqm.model.YRCompiledReport;
import lombok.Getter;

@Getter
public class YRSovVsAlliedReport extends YRCompiledReport {

    private int sovWins;
    private int alliedWins;

    public YRSovVsAlliedReport(String mapName) {
        super(mapName);
    }

    public void addAlliedWin() {
        alliedWins++;
    }

    public void addSovWin() {
        sovWins++;
    }

    public boolean hasGames() {
        return sovWins > 0 || alliedWins > 0;
    }

    @Override
    public String toString() {
        return "[" + mapName + "] " +
                "sovWins=" + sovWins +
                ", alliedWins=" + alliedWins;
    }

    @Override
    public String getWinsLosses() {
        return "Soviet wins: " + sovWins + " / Allied Wins: " + alliedWins;
    }

    @Override
    public int getTotalGames() {
        return sovWins + alliedWins;
    }
}