package org.cncnet.yrqm.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cncnet.yrqm.model.QMReport;

import java.util.*;
import java.util.stream.Collectors;

public class QMPlayerFilter {

    private static final Logger logger = LogManager.getLogger(QMPlayerFilter.class.getName());

    /**
     * Filter out players who do not have at least numGamesPlayed games played
     *
     * @param numGamesPlayed filter out players who do not have this many games played
     * @return the list of filtered YR reports
     */
    public static QMReport[] filterPlayersByGamesPlayed(List<QMReport> qmReports, int numGamesPlayed) {

        HashMap<String, Integer> yrReportsPerPlayer = new HashMap<>();  //key = player name, value = # games played

        for (QMReport gameReport : qmReports) { //loop through all game reports, sum up all games played per player

            if (gameReport.getPlayers() == null || gameReport.getPlayers().length < 2) //skip any report missing player info
                continue;

            QMReport.QMPlayer QMPlayer1 = gameReport.getPlayers()[0];
            QMReport.QMPlayer QMPlayer2 = gameReport.getPlayers()[1];

            if (QMPlayer1.getDisconnected() > 0 || QMPlayer2.getDisconnected() > 0
                    || QMPlayer1.getNo_completion() > 0 || QMPlayer2.getNo_completion() > 0) //skip games that DC'd or did not finish
                continue;

            String player1 = gameReport.getPlayers()[0].getName();
            String player2 = gameReport.getPlayers()[1].getName();

            Integer numReportsP1 = yrReportsPerPlayer.get(player1);
            Integer numReportsP2 = yrReportsPerPlayer.get(player2);

            if (numReportsP1 == null)
                numReportsP1 = 0;

            if (numReportsP2 == null)
                numReportsP2 = 0;

            yrReportsPerPlayer.put(player1, numReportsP1 + 1);
            yrReportsPerPlayer.put(player2, numReportsP2 + 1);
        }

        List<QMReport> filteredReports = new ArrayList<>();

        for (QMReport gameReport : qmReports) { //filter out games
            if (gameReport.getPlayers() == null || gameReport.getPlayers().length < 2) //skip any report missing player info
                continue;

            QMReport.QMPlayer QMPlayer1 = gameReport.getPlayers()[0];
            QMReport.QMPlayer QMPlayer2 = gameReport.getPlayers()[1];

            if (QMPlayer1.getDisconnected() > 0 || QMPlayer2.getDisconnected() > 0
                    || QMPlayer1.getNo_completion() > 0 || QMPlayer2.getNo_completion() > 0) //skip games that DC'd or did not finish
                continue;

            if (yrReportsPerPlayer.get(QMPlayer1.getName()) > numGamesPlayed && yrReportsPerPlayer.get(QMPlayer2.getName()) > numGamesPlayed) {
                filteredReports.add(gameReport);
            }
        }

        logger.info("Filtered down to " + filteredReports.size() + " reports, players who did not have " + numGamesPlayed + " games played removed from dataset");

        return filteredReports.toArray(new QMReport[0]);
    }

    /**
     * @param gameReports
     * @param rankMax
     * @return reports only with players in the top rankMax
     */
    public static QMReport[] filterPlayersByRank(QMReport[] gameReports, int rankMax) {

        TreeMap<String, Integer> playerPoints = new TreeMap<>();

        for (QMReport report : gameReports) {

            for (QMReport.QMPlayer player : report.getPlayers()) {
                String name = player.getName();

                if (playerPoints.containsKey(name)) {
                    int count = playerPoints.get(name) + player.getPoints();
                    playerPoints.put(name, count);
                } else {
                    playerPoints.put(name, player.getPoints());
                }
            }
        }

        List<String> keys = new ArrayList<>(playerPoints.keySet());
        keys = keys.stream()
                .sorted(Comparator.comparing(playerPoints::get).reversed())
                .collect(Collectors.toList());

        int min = Math.min(rankMax, keys.size());
        keys = keys.subList(0, min); //this list will have the player names in the top rankMax

        List<QMReport> filteredReports = new ArrayList<>();

        for (QMReport report : gameReports) {

            String player1 = report.getPlayers()[0].getName();
            String player2 = report.getPlayers()[1].getName();

            if (keys.contains(player1) && keys.contains(player2)) {
                filteredReports.add(report);
            }
        }

        System.out.println();
        System.out.println("Filtered reports down to players ranked in the top: " + rankMax);
        System.out.println();

        return filteredReports.toArray(new QMReport[]{});
    }
}
