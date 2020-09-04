package org.cncnet.yrqm.model.reports;

import org.cncnet.yrqm.model.YRCompiledReport;
import lombok.Getter;

@Getter
public class YRSovVsYuriReport extends YRCompiledReport {

    private int sovWins;
    private int yuriWins;

    public YRSovVsYuriReport(String mapName) {
        super(mapName);
    }

    @Override
    public String getWinsLosses() {
        return "Soviet wins: " + sovWins + " / Yuri Wins: " + yuriWins;
    }

    @Override
    public int getTotalGames() {
        return sovWins + yuriWins;
    }

    public void addYuriWin() {
        yuriWins++;
    }

    public void addSovWin() {
        sovWins++;
    }

    public boolean hasGames() {
        return sovWins > 0 || yuriWins > 0;
    }

    @Override
    public String toString() {
        return "[" + mapName + "] " +
                "yuriWins=" + yuriWins +
                ", sovWins=" + sovWins;
    }
}