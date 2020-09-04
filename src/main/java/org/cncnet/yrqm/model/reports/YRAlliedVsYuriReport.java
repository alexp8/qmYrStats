package org.cncnet.yrqm.model.reports;

import org.cncnet.yrqm.model.YRCompiledReport;
import lombok.Getter;

@Getter
public class YRAlliedVsYuriReport extends YRCompiledReport {

    private int alliedWins;
    private int yuriWins;

    public YRAlliedVsYuriReport(String mapName) {
        super(mapName);
    }

    public void addAlliedWin() {
        alliedWins++;
    }

    public void addYuriWin() {
        yuriWins++;
    }

    public boolean hasGames() {
        return alliedWins > 0 || yuriWins > 0;
    }

    @Override
    public int getTotalGames() {
        return alliedWins + yuriWins;
    }

    @Override
    public String toString() {
        return "[" + mapName + "] " +
                "yuriWins=" + yuriWins +
                ", alliedWins=" + alliedWins;
    }

    @Override
    public String getWinsLosses() {
        return "Allied wins: " + alliedWins + " / Yuri Wins: " + yuriWins;
    }
}