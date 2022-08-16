package org.cncnet.yrqm.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cncnet.yrqm.model.PlayerLookup;
import org.cncnet.yrqm.model.QMReport;
import org.cncnet.yrqm.model.enums.QMGame;
import org.cncnet.yrqm.parser.QMElo;
import org.cncnet.yrqm.parser.QMPlayerFilter;
import org.cncnet.yrqm.parser.QMReportParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QMReportMain {
    private static final Logger logger = LogManager.getLogger();

    private QMReportMain() {
    }

    public static void main(String... args) throws IOException {
        final Path path = Paths.get("src/main/resources/qm-bulk/8-2022-bulk.json");

        final QMReportParser qmReportParser = new QMReportParser();

        final QMGame[] qmGames = new QMGame[]{
//                QMGame.RA2,
//                QMGame.YR,
//                QMGame.RA,
//                QMGame.TS,
//                QMGame.SFJ,
                QMGame.BLITZ};

        final QMGame[] qmGamesPlayerMatchupReports = new QMGame[]{ //generate player matchup reports for listed ladder games
                QMGame.BLITZ
        };

        final Map<String, List<QMReport>> qmGameReports = qmReportParser.getQmReport(path);

        for (final QMGame qmGame : qmGames) {
            if (!qmGameReports.containsKey(qmGame.getGame())) {
                logger.warn(qmGame.getGame() + " is not found from: " + qmGameReports.keySet() + " . Skipping.");
                continue;
            }

            System.out.println("\n====================================================================");
            System.out.println("================= Generating report data for " + qmGame.getGame() + " =================");
            System.out.println("====================================================================");

            final List<QMReport> qmReports = qmGameReports.get(qmGame.getGame());

            System.out.println("Total games played: " + qmReports.size());
            QMReport[] gameReports_filtered = QMPlayerFilter.filterPlayersByGamesPlayed(qmReports, 5); //filter out games that disconnected, and any games played by player with less than x total played games

            qmReportParser.printTotalDurationOfGamesPlayed(gameReports_filtered);

            qmReportParser.generateNumberOfPlayers(gameReports_filtered, 10); //display how many players played at least 10 games

            gameReports_filtered = QMPlayerFilter.filterPlayersByRank(gameReports_filtered, 100); //get games played by top x ranked players

            qmReportParser.parseReportsByMatchup(gameReports_filtered); //calculate how many games were played by each faction

            qmReportParser.generateTotalGamesPlayedPerMap(gameReports_filtered); //calculate how many maps played on each map

            qmReportParser.parseReportsByMap(gameReports_filtered); //calculate the wins by each side per map

//            QMElo.generateElo(gameReports_filtered, 1000); //generate ELO ratings for each faction on each map

            if (Arrays.stream(qmGamesPlayerMatchupReports)
                    .anyMatch(x -> x.getGame().equals(qmGame.getGame())))
                generatePlayerMatchupReports(qmGame, qmReportParser, gameReports_filtered); //generate player matchups
        }
    }

    /**
     * Generate the player matchup winrate data
     *
     * @param qmGame               ladder game
     * @param qmReportParser       qm parser
     * @param gameReports_filtered qm reports
     * @throws IOException throw exception
     */
    private static void generatePlayerMatchupReports(QMGame qmGame, QMReportParser qmReportParser, QMReport[] gameReports_filtered) throws IOException {
        Map<String, List<String>> playerLookups = new HashMap<>();
        Path lookupPath = Paths.get("src", "main", "resources", "lookup", qmGame.getGame() + "_lookup.json");
        if (Files.exists(lookupPath)) {
            String content = Files.readString(lookupPath, StandardCharsets.UTF_8);
            playerLookups = PlayerLookup.getPlayerLookups(content);
        }

        Path playersDirPath = Paths.get("src", "main", "resources", "playerReports");
        Path blitzFile = Paths.get(playersDirPath.toAbsolutePath().toString(), qmGame.getGame());
        if (!Files.exists(blitzFile))
            Files.createDirectory(blitzFile);
        Path reportsFile = Paths.get(blitzFile.toAbsolutePath().toString(), qmGame.getGame() + ".csv");

        if (Files.exists(reportsFile)) {
            Files.delete(reportsFile);
        }

        try {
            Files.createFile(reportsFile.toAbsolutePath());
            final String header = "PlayerA,PlayerAWins,PlayerB,PlayerBWins";
            Files.writeString(reportsFile, header + System.getProperty("line.separator"), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file at: " + reportsFile, e);
        }

        qmReportParser.createPlayerMatchupStats(qmGame.getGame(), gameReports_filtered, reportsFile, playerLookups);
    }
}