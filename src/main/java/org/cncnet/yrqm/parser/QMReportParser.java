package org.cncnet.yrqm.parser;

import com.google.gson.Gson;
import org.cncnet.yrqm.config.YRConfig;
import org.cncnet.yrqm.model.QMReport;
import org.cncnet.yrqm.model.YRCompiledReport;
import org.cncnet.yrqm.model.enums.YRFactionEnum;
import org.cncnet.yrqm.model.reports.YRAlliedVsYuriReport;
import org.cncnet.yrqm.model.reports.YRSovVsAlliedReport;
import org.cncnet.yrqm.model.reports.YRSovVsYuriReport;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class QMReportParser {

    private final YRConfig yrConfig;
    private int mirrorMatches;

    public QMReportParser() {
        yrConfig = new YRConfig();
    }

    /**
     * Calculate the stats for every matchup in every map.
     *
     * @param yrReports
     */
    public void parseYRReportsByMap(QMReport.YRReport[] yrReports) {
        HashMap<String, List<QMReport.YRReport>> reportsOrganizedByMap = getYRReportsByMap(yrReports);

        List<YRCompiledReport> yrCompiledReports = compileYRReportsByMap(reportsOrganizedByMap); //calculate the matchup statistics for every map

        Collections.sort(yrCompiledReports); //sort by Standard maps

        yrCompiledReports.forEach(System.out::println);

//        YRGraphGenerator.createPieChartsFromMatchupsPerMap(yrCompiledReports);
    }

    /**
     * Calculate the matchup statistics for every map
     *
     * @param reportsOrganizedByMap all of the YRReports organized by map
     * @return a list of reports containg the matchup statistics for every matchup on every map. Mirror matchups excluded
     */
    private List<YRCompiledReport> compileYRReportsByMap(HashMap<String, List<QMReport.YRReport>> reportsOrganizedByMap) {
        List<YRCompiledReport> yrCompiledReports = new ArrayList<>();

        for (String key : reportsOrganizedByMap.keySet()) { //loop through every matchup per map, add up the wins/losses by faction
            List<QMReport.YRReport> yrReportsList = reportsOrganizedByMap.get(key);
            QMReport.YRReport[] yrReportsArr = yrReportsList.toArray(new QMReport.YRReport[0]);

            //one report for each possible matchup on the map
            YRSovVsYuriReport yrSovVsYuriReport = new YRSovVsYuriReport(key);
            YRAlliedVsYuriReport yrAlliedVsYuriReport = new YRAlliedVsYuriReport(key);
            YRSovVsAlliedReport yrSovVsAlliedReport = new YRSovVsAlliedReport(key);

            addWinsPerMatchup(yrReportsArr, yrSovVsYuriReport, yrAlliedVsYuriReport, yrSovVsAlliedReport);

            if (yrSovVsYuriReport.hasGames())
                yrCompiledReports.add(yrSovVsYuriReport);
            if (yrAlliedVsYuriReport.hasGames())
                yrCompiledReports.add(yrAlliedVsYuriReport);
            if (yrSovVsAlliedReport.hasGames())
                yrCompiledReports.add(yrSovVsAlliedReport);
        }

        return yrCompiledReports;
    }

    /**
     * Given a list of YRReports, organize them into a map, each key being a map name
     *
     * @param yrReports Yuri's revenge QM reports
     * @return a hashmap containing all of the YRReports organized by map name
     */
    private HashMap<String, List<QMReport.YRReport>> getYRReportsByMap(QMReport.YRReport[] yrReports) {
        HashMap<String, List<QMReport.YRReport>> reportsOrganizedByMap = new HashMap<>();

        for (QMReport.YRReport yrReport : yrReports) {//parse YR reports by map

            String mapName = yrReport.getScen();

            if (!reportsOrganizedByMap.containsKey(mapName)) { //have we not seen this map yet
                List<QMReport.YRReport> list = new ArrayList<>();
                list.add(yrReport);

                reportsOrganizedByMap.put(mapName, list);
            } else {
                List<QMReport.YRReport> list = reportsOrganizedByMap.get(mapName);
                list.add(yrReport);

                reportsOrganizedByMap.put(mapName, list);
            }
        }

        return reportsOrganizedByMap;
    }

    public void parseYRReportsByMatchup(QMReport.YRReport[] yrReports) {
        List<YRCompiledReport> reportsOrganizedByMatchup = getYRReportsByMatchup(yrReports); //Compile the reports into a list based on matchups

        int totalGames = reportsOrganizedByMatchup.stream().filter(x -> x.getTotalGames() > 0).mapToInt(YRCompiledReport::getTotalGames).sum() + mirrorMatches;
        System.out.println("Total YR QM games: " + totalGames);
        System.out.println("");
        System.out.println("Mirror matches: " + mirrorMatches);
        System.out.println("");

        reportsOrganizedByMatchup.forEach(x -> System.out.println(x.getWinsLosses()));
    }

    private List<YRCompiledReport> getYRReportsByMatchup(QMReport.YRReport[] yrReports) {

        List<YRCompiledReport> yrReportsByMatchup = new ArrayList<>();

        YRSovVsYuriReport yrSovVsYuriReport = new YRSovVsYuriReport(null);
        YRAlliedVsYuriReport yrAlliedVsYuriReport = new YRAlliedVsYuriReport(null);
        YRSovVsAlliedReport yrSovVsAlliedReport = new YRSovVsAlliedReport(null);

        addWinsPerMatchup(yrReports, yrSovVsYuriReport, yrAlliedVsYuriReport, yrSovVsAlliedReport);

        yrReportsByMatchup.add(yrSovVsYuriReport);
        yrReportsByMatchup.add(yrAlliedVsYuriReport);
        yrReportsByMatchup.add(yrSovVsAlliedReport);

        return yrReportsByMatchup;
    }

    private void addWinsPerMatchup(QMReport.YRReport[] yrReports, YRSovVsYuriReport yrSovVsYuriReport, YRAlliedVsYuriReport yrAlliedVsYuriReport, YRSovVsAlliedReport yrSovVsAlliedReport) {
        for (QMReport.YRReport yrReport : yrReports) { //loop through all of the YR reports for this map

            if (yrReport.getPlayers() == null || yrReport.getPlayers().length < 2)
                continue;

            QMReport.YRReport.YRPlayer yrPlayer1 = yrReport.getPlayers()[0];
            QMReport.YRReport.YRPlayer yrPlayer2 = yrReport.getPlayers()[1];

            if (yrPlayer1.getDisconnected() > 0 || yrPlayer2.getDisconnected() > 0
                    || yrPlayer1.getNo_completion() > 0 || yrPlayer2.getNo_completion() > 0) //skip games that DC'd or did not finish
                continue;

            int player1CountryId = yrPlayer1.getCountry();
            int player2CountryId = yrPlayer2.getCountry();

            YRFactionEnum player1Faction = yrConfig.getFactions().get(player1CountryId);
            YRFactionEnum player2Faction = yrConfig.getFactions().get(player2CountryId);

            if ((player1Faction == YRFactionEnum.SOVIET && player2Faction == YRFactionEnum.YURI)
                    || (player2Faction == YRFactionEnum.SOVIET && player1Faction == YRFactionEnum.YURI)) { //sov vs yuri

                if ((yrPlayer1.getWon() > 0 && player1Faction == YRFactionEnum.SOVIET)
                        || (yrPlayer2.getWon() > 0 && player2Faction == YRFactionEnum.SOVIET)) //did sov win
                    yrSovVsYuriReport.addSovWin();
                else
                    yrSovVsYuriReport.addYuriWin();

            } else if ((player1Faction == YRFactionEnum.SOVIET && player2Faction == YRFactionEnum.ALLIED)
                    || (player2Faction == YRFactionEnum.SOVIET && player1Faction == YRFactionEnum.ALLIED)) { //sov vs allied

                if ((yrPlayer1.getWon() > 0 && player1Faction == YRFactionEnum.ALLIED)
                        || (yrPlayer2.getWon() > 0 && player2Faction == YRFactionEnum.ALLIED)) //did Allied win
                    yrSovVsAlliedReport.addAlliedWin();
                else
                    yrSovVsAlliedReport.addSovWin();

            } else if ((player1Faction == YRFactionEnum.YURI && player2Faction == YRFactionEnum.ALLIED)
                    || (player2Faction == YRFactionEnum.YURI && player1Faction == YRFactionEnum.ALLIED)) { //yuri vs allied

                if ((yrPlayer1.getWon() > 0 && player1Faction == YRFactionEnum.ALLIED)
                        || (yrPlayer2.getWon() > 0 && player2Faction == YRFactionEnum.ALLIED)) //did Allied win
                    yrAlliedVsYuriReport.addAlliedWin();
                else
                    yrAlliedVsYuriReport.addYuriWin();

            } else
                mirrorMatches++;
        }
    }
}