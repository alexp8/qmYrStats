package org.cncnet.yrqm.parser;

import com.google.gson.Gson;
import org.cncnet.yrqm.config.YRConfig;
import org.cncnet.yrqm.model.QMReport;
import org.cncnet.yrqm.model.enums.YRFactionEnum;

import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class QMElo {

    private static final TreeMap<String, Double> sideElo_HashMap = new TreeMap<>();  //key would be like "Soviet"
    private static final TreeMap<String, Double> sideMapElo_HashMap = new TreeMap<>(); //key would be like "Divide And Conquer: Soviet"
    private static final TreeMap<String, Double> sideVsSideMap_HashMap = new TreeMap<>(); //key would be like "Divide And Conquer: Soviet vs Allies"

    private static final YRConfig yrConfig = new YRConfig();

    private static final double BASE_ELO = 1200;
    private static final int K = 32;

    public static void generateElo(Reader reader, int numIterations) {
        Gson gson = new Gson();

        QMReport.YRReport[] yrReports = gson.fromJson(reader, QMReport.class).getYr(); //parse JSON content into Java objects, hold as array of YR reports

        generateElo(yrReports, numIterations);
    }

    /**
     * Calculate the elo rankings
     */
    public static void generateElo(QMReport.YRReport[] yrReports, int numIterations) {

        for (int i = 0; i < numIterations; i++) {
            calculateElo(yrReports); //generate elo ratings from yr reports
        }

        System.out.println("\n" + numIterations + " iterations executed\n");

        System.out.println("============================================");
        System.out.println("=============  ELO Rankings  ===============");
        System.out.println("============================================");


        /* ** Print out the elo rankings ** */
        printSideElo(); //print the overall Elo results of a side

        printSideMapElo(); //print the Elo results of a side playing on a map

        printSideMapVsSideElo(); //print the Elo results of a side playing on a map vs specific faction matchup
    }

    private static void printSideElo() {
        double avgElo = (double) sideElo_HashMap.values().stream().mapToInt(Double::intValue).sum() / sideElo_HashMap.values().size();
        System.out.println("-Average elo: " + avgElo);

        for (String key : sideElo_HashMap.keySet()) {
            System.out.println(key + ": " + sideElo_HashMap.get(key));
        }

        System.out.println();
    }

    private static void printSideMapVsSideElo() {
        double avgElo;
        avgElo = (double) sideVsSideMap_HashMap.values().

                stream().

                mapToInt(Double::intValue).

                sum() / sideVsSideMap_HashMap.values().

                size();
        System.out.println("-Average elo: " + avgElo);
        for (
                String key : sideVsSideMap_HashMap.keySet()) {
            System.out.println(key + ": " + sideVsSideMap_HashMap.get(key));
        }
    }

    private static void printSideMapElo() {
        double avgElo;
        avgElo = (double) sideMapElo_HashMap.values().stream().mapToInt(Double::intValue).sum() / sideMapElo_HashMap.values().size();
        System.out.println("-Average elo: " + avgElo);

        //sort all of the ratings, allied elo ratings and then soviet
        List<String> alliedKeys = sideMapElo_HashMap.keySet().stream().filter(x -> x.contains("Allied")).collect(Collectors.toList());
        List<String> sovietKeys = sideMapElo_HashMap.keySet().stream().filter(x -> x.contains("Soviet")).collect(Collectors.toList());

        alliedKeys = alliedKeys.stream().sorted(Comparator.comparing(sideMapElo_HashMap::get)).collect(Collectors.toList());

        sovietKeys = sovietKeys.stream().sorted(Comparator.comparing(sideMapElo_HashMap::get)).collect(Collectors.toList());

        for (String key : alliedKeys) {
            System.out.println(key + ": " + sideMapElo_HashMap.get(key));
        }

        System.out.println();

        for (String key : sovietKeys) {
            System.out.println(key + ": " + sideMapElo_HashMap.get(key));
        }

        System.out.println("\n");
    }

    private static void calculateElo(QMReport.YRReport[] yrReports) {

        for (QMReport.YRReport yrReport : yrReports) { //loop through all of the YR reports

            if (yrReport.getPlayers() == null || yrReport.getPlayers().length < 2) //skip any report missing player info
                continue;

            QMReport.YRReport.YRPlayer yrPlayer1 = yrReport.getPlayers()[0];
            QMReport.YRReport.YRPlayer yrPlayer2 = yrReport.getPlayers()[1];

            if (yrPlayer1.getDisconnected() > 0 || yrPlayer2.getDisconnected() > 0
                    || yrPlayer1.getNo_completion() > 0 || yrPlayer2.getNo_completion() > 0) //skip games that DC'd or did not finish
                continue;

            String mapName = yrReport.getScen();

            YRFactionEnum player1Faction = yrConfig.getFactions().get(yrPlayer1.getCountry()); //get p1 faction
            YRFactionEnum player2Faction = yrConfig.getFactions().get(yrPlayer2.getCountry()); //get p2 faction

            if ((player1Faction == YRFactionEnum.SOVIET && player2Faction == YRFactionEnum.YURI)
                    || (player2Faction == YRFactionEnum.SOVIET && player1Faction == YRFactionEnum.YURI)) { //sov vs yuri

                if ((yrPlayer1.getWon() > 0 && player1Faction == YRFactionEnum.SOVIET)
                        || (yrPlayer2.getWon() > 0 && player2Faction == YRFactionEnum.SOVIET)) { //did sov win

                    elo(mapName, YRFactionEnum.SOVIET.getName(), YRFactionEnum.YURI.getName());

                } else {
                    elo(mapName, YRFactionEnum.YURI.getName(), YRFactionEnum.SOVIET.getName()); //yuri wins
                }

            } else if ((player1Faction == YRFactionEnum.SOVIET && player2Faction == YRFactionEnum.ALLIED)
                    || (player2Faction == YRFactionEnum.SOVIET && player1Faction == YRFactionEnum.ALLIED)) { //sov vs allied

                if ((yrPlayer1.getWon() > 0 && player1Faction == YRFactionEnum.ALLIED)
                        || (yrPlayer2.getWon() > 0 && player2Faction == YRFactionEnum.ALLIED)) { //did Allied win

                    elo(mapName, YRFactionEnum.ALLIED.getName(), YRFactionEnum.SOVIET.getName());

                } else {
                    elo(mapName, YRFactionEnum.SOVIET.getName(), YRFactionEnum.ALLIED.getName());
                }

            } else if ((player1Faction == YRFactionEnum.YURI && player2Faction == YRFactionEnum.ALLIED)
                    || (player2Faction == YRFactionEnum.YURI && player1Faction == YRFactionEnum.ALLIED)) { //yuri vs allied

                if ((yrPlayer1.getWon() > 0 && player1Faction == YRFactionEnum.ALLIED)
                        || (yrPlayer2.getWon() > 0 && player2Faction == YRFactionEnum.ALLIED)) {//did Allied win

                    elo(mapName, YRFactionEnum.ALLIED.getName(), YRFactionEnum.YURI.getName());

                } else {
                    elo(mapName, YRFactionEnum.YURI.getName(), YRFactionEnum.ALLIED.getName());
                }
            }
        }
    }

    private static void elo(String mapName, String faction1, String faction2) {
        String key1 = faction1;
        String key2 = faction2;

        /* Side Elo */
        calculateSideElo(sideElo_HashMap, key1, key2);

        /* Side Map Elo */
        key1 = "[" + mapName + "] " + faction1;
        key2 = "[" + mapName + "] " + faction2;
        calculateSideElo(sideMapElo_HashMap, key1, key2);

        /* Side Map vs Side Elo */
        key1 = "[" + mapName + "] " + faction1 + " vs " + faction2;
        key2 = "[" + mapName + "] " + faction2 + " vs " + faction1;
        calculateSideElo(sideVsSideMap_HashMap, key1, key2);
    }

    private static void calculateSideElo(TreeMap<String, Double> map, String key1, String key2) {
        map.putIfAbsent(key1, BASE_ELO);
        map.putIfAbsent(key2, BASE_ELO);

        Double elo1 = map.get(key1);
        Double elo2 = map.get(key2);

        double new_elo1 = eloRating(elo1, elo2, true);
        double new_elo2 = eloRating(elo2, elo1, false);

        map.put(key1, new_elo1);
        map.put(key2, new_elo2);
    }

    /**
     * @param elo1       player 1 elo
     * @param elo2       player 2 elo
     * @param didElo1Win if player 1 won, generate their elo positively
     */
    private static double eloRating(double elo1, double elo2, boolean didElo1Win) {

        double p1 = 1.0 * 1.0 / (1 + 1.0 *
                pow(10, 1.0 * (elo2 - elo1) / 400));

        if (didElo1Win) {
            return elo1 + K * (1 - p1);
        } else {
            return elo1 + K * (0 - p1);
        }
    }

    public static void main(String... args) {
        double newElo = eloRating(1000, 1100, true);

        System.out.println(newElo);
    }
}