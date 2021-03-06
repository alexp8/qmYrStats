package org.cncnet.yrqm.parser;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cncnet.yrqm.model.QMReport;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class QMPlayerFilter {

    private static final Logger logger = LogManager.getLogger(QMPlayerFilter.class.getName());

    /**
     * Filter out players who do not have at least i games played
     *
     * @param numGamesPlayed filter out players who do not have this many games played
     * @return the list of filtered YR reports
     */
    public static QMReport.YRReport[] filterPlayers(Path[] paths, int numGamesPlayed) throws IOException {
        Gson gson = new Gson();

        List<QMReport.YRReport> yrReports = new ArrayList<>();

        for (Path path : paths) { //parse all json files and gather the reports
            BufferedReader reader = Files.newBufferedReader(path);
            QMReport.YRReport[] yrReports1 = gson.fromJson(reader, QMReport.class).getYr(); //parse JSON content into Java objects, hold as array of YR reports
            logger.info(yrReports1.length + " YR reports found in input json");
            yrReports.addAll(Arrays.asList(yrReports1));
        }

        HashMap<String, Integer> yrReportsPerPlayer = new HashMap<>();  //key = player name, value = # games played

        for (QMReport.YRReport yrReport : yrReports) { //loop through all YR reports, sum up all games played per player

            if (yrReport.getPlayers() == null || yrReport.getPlayers().length < 2) //skip any report missing player info
                continue;

            QMReport.YRReport.YRPlayer yrPlayer1 = yrReport.getPlayers()[0];
            QMReport.YRReport.YRPlayer yrPlayer2 = yrReport.getPlayers()[1];

            if (yrPlayer1.getDisconnected() > 0 || yrPlayer2.getDisconnected() > 0
                    || yrPlayer1.getNo_completion() > 0 || yrPlayer2.getNo_completion() > 0) //skip games that DC'd or did not finish
                continue;

            String player1 = yrReport.getPlayers()[0].getName();
            String player2 = yrReport.getPlayers()[1].getName();

            Integer numReportsP1 = yrReportsPerPlayer.get(player1);
            Integer numReportsP2 = yrReportsPerPlayer.get(player2);

            if (numReportsP1 == null)
                numReportsP1 = 0;

            if (numReportsP2 == null)
                numReportsP2 = 0;

            yrReportsPerPlayer.put(player1, numReportsP1 + 1);
            yrReportsPerPlayer.put(player2, numReportsP2 + 1);
        }

        List<QMReport.YRReport> filteredReports = new ArrayList<>();

        for (QMReport.YRReport yrReport : yrReports) { //filter out games
            if (yrReport.getPlayers() == null || yrReport.getPlayers().length < 2) //skip any report missing player info
                continue;

            QMReport.YRReport.YRPlayer yrPlayer1 = yrReport.getPlayers()[0];
            QMReport.YRReport.YRPlayer yrPlayer2 = yrReport.getPlayers()[1];

            if (yrPlayer1.getDisconnected() > 0 || yrPlayer2.getDisconnected() > 0
                    || yrPlayer1.getNo_completion() > 0 || yrPlayer2.getNo_completion() > 0) //skip games that DC'd or did not finish
                continue;

            if (yrReportsPerPlayer.get(yrPlayer1.getName()) > numGamesPlayed && yrReportsPerPlayer.get(yrPlayer2.getName()) > numGamesPlayed) {
                filteredReports.add(yrReport);
            }
        }

        logger.info("Filtered down to " + filteredReports.size() + " reports, players who did not have " + numGamesPlayed + " games played removed from dataset");

        return filteredReports.toArray(new QMReport.YRReport[0]);
    }
}
