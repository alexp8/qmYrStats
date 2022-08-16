package org.cncnet.yrqm.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.cncnet.yrqm.config.YRConfig;
import org.cncnet.yrqm.model.QMPlayerMatchupReport;
import org.cncnet.yrqm.model.QMReport;
import org.cncnet.yrqm.model.YRCompiledReport;
import org.cncnet.yrqm.model.YRMapComparator;
import org.cncnet.yrqm.model.enums.YRFactionEnum;
import org.cncnet.yrqm.model.reports.YRAlliedVsYuriReport;
import org.cncnet.yrqm.model.reports.YRSovVsAlliedReport;
import org.cncnet.yrqm.model.reports.YRSovVsYuriReport;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class QMReportParser {

    private final YRConfig yrConfig;
    private int mirrorMatches;

    public QMReportParser() {
        yrConfig = new YRConfig();
    }

    /**
     * Parse json file and return the QM reports of the provided QM game
     *
     * @param path path to report file
     * @return the qm reports of given game
     */
    public Map<String, List<QMReport>> getQmReport(Path path) throws IOException {
        final String content = Files.readString(path, StandardCharsets.US_ASCII);
        final Type mapType = new TypeToken<Map<String, List<QMReport>>>() {
        }.getType();
        return new Gson().fromJson(content, mapType);
    }

    /**
     * Calculate the stats for every matchup in every map.
     *
     * @param gameReports qm reports
     */
    public void parseReportsByMap(QMReport[] gameReports) {
        HashMap<String, List<QMReport>> reportsOrganizedByMap = getYRReportsByMap(gameReports);

        List<YRCompiledReport> yrCompiledReports = compileYRReportsByMap(reportsOrganizedByMap); //calculate the matchup statistics for every map

        Collections.sort(yrCompiledReports); //sort by Standard maps

        yrCompiledReports.forEach(System.out::println);
    }

    /**
     * Calculate the matchup statistics for every map
     *
     * @param reportsOrganizedByMap all of the YRReports organized by map
     * @return a list of reports containg the matchup statistics for every matchup on every map. Mirror matchups excluded
     */
    private List<YRCompiledReport> compileYRReportsByMap(HashMap<String, List<QMReport>> reportsOrganizedByMap) {
        List<YRCompiledReport> yrCompiledReports = new ArrayList<>();

        for (String key : reportsOrganizedByMap.keySet()) { //loop through every matchup per map, add up the wins/losses by faction
            List<QMReport> gameReportsList = reportsOrganizedByMap.get(key);
            QMReport[] gameReportsArr = gameReportsList.toArray(new QMReport[0]);

            //one report for each possible matchup on the map
            YRSovVsYuriReport yrSovVsYuriReport = new YRSovVsYuriReport(key);
            YRAlliedVsYuriReport yrAlliedVsYuriReport = new YRAlliedVsYuriReport(key);
            YRSovVsAlliedReport yrSovVsAlliedReport = new YRSovVsAlliedReport(key);

            addWinsPerMatchup(gameReportsArr, yrSovVsYuriReport, yrAlliedVsYuriReport, yrSovVsAlliedReport);

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
     * @param gameReports Yuri's revenge QM reports
     * @return a hashmap containing all of the YRReports organized by map name
     */
    private HashMap<String, List<QMReport>> getYRReportsByMap(QMReport[] gameReports) {
        HashMap<String, List<QMReport>> reportsOrganizedByMap = new HashMap<>();

        for (QMReport gameReport : gameReports) {//parse YR reports by map

            String mapName = gameReport.getScen();

            List<QMReport> list;
            if (!reportsOrganizedByMap.containsKey(mapName)) { //have we not seen this map yet
                list = new ArrayList<>();
            } else {
                list = reportsOrganizedByMap.get(mapName);
            }
            list.add(gameReport);
            reportsOrganizedByMap.put(mapName, list);
        }

        return reportsOrganizedByMap;
    }

    public void parseReportsByMatchup(QMReport[] gameReports) {
        mirrorMatches = 0;
        List<YRCompiledReport> reportsOrganizedByMatchup = getReportsByMatchup(gameReports); //Compile the reports into a list based on matchups

        int totalGames = reportsOrganizedByMatchup.stream().filter(x -> x.getTotalGames() > 0).mapToInt(YRCompiledReport::getTotalGames).sum() + mirrorMatches;
        System.out.println("Total QM games: " + totalGames);
        System.out.println();
        System.out.println("Mirror matches: " + mirrorMatches);
        System.out.println();

        reportsOrganizedByMatchup.forEach(x -> System.out.println(x.getWinsLosses()));
    }

    private List<YRCompiledReport> getReportsByMatchup(QMReport[] gameReports) {
        List<YRCompiledReport> yrReportsByMatchup = new ArrayList<>();

        YRSovVsYuriReport yrSovVsYuriReport = new YRSovVsYuriReport(null);
        YRAlliedVsYuriReport yrAlliedVsYuriReport = new YRAlliedVsYuriReport(null);
        YRSovVsAlliedReport yrSovVsAlliedReport = new YRSovVsAlliedReport(null);

        addWinsPerMatchup(gameReports, yrSovVsYuriReport, yrAlliedVsYuriReport, yrSovVsAlliedReport);

        yrReportsByMatchup.add(yrSovVsYuriReport);
        yrReportsByMatchup.add(yrAlliedVsYuriReport);
        yrReportsByMatchup.add(yrSovVsAlliedReport);

        return yrReportsByMatchup;
    }

    private void addWinsPerMatchup(QMReport[] gameReports, YRSovVsYuriReport yrSovVsYuriReport, YRAlliedVsYuriReport yrAlliedVsYuriReport, YRSovVsAlliedReport yrSovVsAlliedReport) {
        for (QMReport gameReport : gameReports) { //loop through all of the YR reports for this map

            if (gameReport.getPlayers() == null || gameReport.getPlayers().length < 2)
                continue;

            QMReport.QMPlayer QMPlayer1 = gameReport.getPlayers()[0];
            QMReport.QMPlayer QMPlayer2 = gameReport.getPlayers()[1];

            if (QMPlayer1.getDisconnected() > 0 || QMPlayer2.getDisconnected() > 0
                    || QMPlayer1.getNo_completion() > 0 || QMPlayer2.getNo_completion() > 0) //skip games that DC'd or did not finish
                continue;

            int player1CountryId = QMPlayer1.getCountry();
            int player2CountryId = QMPlayer2.getCountry();

            YRFactionEnum player1Faction = yrConfig.getFactions().get(player1CountryId);
            YRFactionEnum player2Faction = yrConfig.getFactions().get(player2CountryId);

            if ((player1Faction == YRFactionEnum.SOVIET && player2Faction == YRFactionEnum.YURI)
                    || (player2Faction == YRFactionEnum.SOVIET && player1Faction == YRFactionEnum.YURI)) { //sov vs yuri

                if ((QMPlayer1.getWon() > 0 && player1Faction == YRFactionEnum.SOVIET)
                        || (QMPlayer2.getWon() > 0 && player2Faction == YRFactionEnum.SOVIET)) //did sov win
                    yrSovVsYuriReport.addSovWin();
                else
                    yrSovVsYuriReport.addYuriWin();

            } else if ((player1Faction == YRFactionEnum.SOVIET && player2Faction == YRFactionEnum.ALLIED)
                    || (player2Faction == YRFactionEnum.SOVIET && player1Faction == YRFactionEnum.ALLIED)) { //sov vs allied

                if ((QMPlayer1.getWon() > 0 && player1Faction == YRFactionEnum.ALLIED)
                        || (QMPlayer2.getWon() > 0 && player2Faction == YRFactionEnum.ALLIED)) //did Allied win
                    yrSovVsAlliedReport.addAlliedWin();
                else
                    yrSovVsAlliedReport.addSovWin();

            } else if ((player1Faction == YRFactionEnum.YURI && player2Faction == YRFactionEnum.ALLIED)
                    || (player2Faction == YRFactionEnum.YURI && player1Faction == YRFactionEnum.ALLIED)) { //yuri vs allied

                if ((QMPlayer1.getWon() > 0 && player1Faction == YRFactionEnum.ALLIED)
                        || (QMPlayer2.getWon() > 0 && player2Faction == YRFactionEnum.ALLIED)) //did Allied win
                    yrAlliedVsYuriReport.addAlliedWin();
                else
                    yrAlliedVsYuriReport.addYuriWin();

            } else
                mirrorMatches++;
        }
    }

    public void generateTotalGamesPlayedPerMap(QMReport[] gameReports_filtered) {

        TreeMap<String, Integer> mapCounts = new TreeMap<>(new YRMapComparator());

        for (QMReport report : gameReports_filtered) {
            String mapName = report.getScen();

            if (mapCounts.containsKey(mapName)) {
                int count = mapCounts.get(mapName) + 1;
                mapCounts.put(mapName, count);
            } else {
                mapCounts.put(mapName, 1);
            }
        }

        System.out.println();
        System.out.println("Games played per map:");

        List<String> keys = new ArrayList<>(mapCounts.keySet());
        keys = keys.stream()
                .sorted(Comparator.comparing(mapCounts::get).reversed())
                .collect(Collectors.toList());

        for (String mapName : keys) {
            System.out.println(mapName + ": " + mapCounts.get(mapName));
        }
        System.out.println();
    }

    public void generateNumberOfPlayers(QMReport[] gameReports_filtered, int numGamesPlayed) {

        TreeMap<String, Integer> playerCount = new TreeMap<>(new YRMapComparator());

        for (QMReport report : gameReports_filtered) {

            for (QMReport.QMPlayer player : report.getPlayers()) {
                String name = player.getName();

                if (playerCount.containsKey(name)) {
                    int count = playerCount.get(name) + 1;
                    playerCount.put(name, count);
                } else {
                    playerCount.put(name, 1);
                }
            }
        }

        List<String> keys = new ArrayList<>(playerCount.keySet());
        keys = keys.stream()
                .filter(a -> playerCount.get(a) >= numGamesPlayed)
                .collect(Collectors.toList());

        System.out.println();
        System.out.println("Number of players with at least 10 games played: " + keys.size());
        System.out.println();
    }

    public void printTotalDurationOfGamesPlayed(QMReport[] gameReports_filtered) {

        double duration = 0;

        for (QMReport gameReport : gameReports_filtered) {
            duration += gameReport.getDuration();
        }

        System.out.println();
        System.out.println("Minutes of total games played: " + (duration / 60));
        System.out.println("Hours of total games played: " + (duration / (60 * 60)));
        System.out.println();
    }

    public Set<String> getPlayerNames(QMReport[] qmReports) {
        Set<String> players = new HashSet<>();

        for (QMReport qmReport : qmReports) {
            for (QMReport.QMPlayer qmPlayer : qmReport.getPlayers()) {
                players.add(qmPlayer.getName());
            }
        }

        return players;
    }

    public void createPlayerMatchupStats(String game, QMReport[] qmReports, Path reportsFile, Map<String, List<String>> playerLookup) throws IOException {
        Set<String> players = getPlayerNames(qmReports);

        List<QMPlayerMatchupReport> qmPlayerMatchupReports = new ArrayList<>();

        //Loop through every player and calculate their matchup win/loss vs opponents
        for (String player : players) {

            final String playerName = getRealName(playerLookup, player); //try and find the qm player's real name from the player name/nick lookup
            QMPlayerMatchupReport qmPlayerMatchupReport = new QMPlayerMatchupReport(game, playerName);

            //Loop through every QM game report and tally player win/loss vs opponents
            for (QMReport qmReport : qmReports) {

                QMReport.QMPlayer me;
                QMReport.QMPlayer opponent;

                if (qmReport.getPlayers()[0].getName().equals(player)) {
                    me = qmReport.getPlayers()[0];
                    opponent = qmReport.getPlayers()[1];
                } else if (qmReport.getPlayers()[1].getName().equals(player)) {
                    me = qmReport.getPlayers()[1];
                    opponent = qmReport.getPlayers()[0];
                } else {
                    continue;
                }

                final String opponentName = getRealName(playerLookup, opponent.getName()); //try and find the opponent's real name from the lookup

                QMPlayerMatchupReport.QMPlayerMatchup qmPlayerMatchup = qmPlayerMatchupReport.getQmPlayerMatchup(opponentName);
                if (qmPlayerMatchup == null) {
                    qmPlayerMatchup = new QMPlayerMatchupReport.QMPlayerMatchup(opponentName);
                    qmPlayerMatchupReport.addMatchup(qmPlayerMatchup);
                }

                if (me.getWon() == 1)
                    qmPlayerMatchup.incrementMyWins();
                else if (opponent.getWon() == 1)
                    qmPlayerMatchup.incrementOpponentWins();
            }

            Collections.sort(qmPlayerMatchupReport.getQmPlayerMatchupList());

            if (!qmPlayerMatchupReport.getQmPlayerMatchupList().isEmpty())
                qmPlayerMatchupReports.add(qmPlayerMatchupReport);
        }

        Collections.sort(qmPlayerMatchupReports); //sort the QM Player matchup reports alphabetically

        for (QMPlayerMatchupReport qmPlayerMatchupReport : qmPlayerMatchupReports) {
            writePlayerGameMatchupReports(reportsFile, qmPlayerMatchupReport);
        }
    }

    /**
     * Write the QM player matchup wins/losses data to a file
     * @param path to write the QM player matchup data
     * @param qmPlayerMatchupReport
     * @throws IOException
     */
    public void writePlayerGameMatchupReports(Path path, QMPlayerMatchupReport qmPlayerMatchupReport) throws IOException {

        //loop through the QmPlayerMatchups and log the wins/losses for the player vs opponents
        for (QMPlayerMatchupReport.QMPlayerMatchup qmPlayerMatchup : qmPlayerMatchupReport.getQmPlayerMatchupList()) {
            String line = qmPlayerMatchupReport.getPlayerName() + "," + qmPlayerMatchup.getMyWins() + ","
                    + qmPlayerMatchup.getOpponentName() + "," + qmPlayerMatchup.getOpponentWins() + System.getProperty("line.separator");

            Files.writeString(path, line, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        }

        Files.writeString(path, System.getProperty("line.separator"), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        Files.writeString(path, System.getProperty("line.separator"), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    /**
     * Pass in a player's nick name and return the players's real name
     *
     * @param playerLookups player mappings of real name to their nicks
     * @param nickname      qm nick
     * @return the player's real name
     */
    public String getRealName(Map<String, List<String>> playerLookups, String nickname) {
        return playerLookups.keySet().stream()
                .filter(x -> playerLookups.get(x).contains(nickname)) //does this nickname belong to a player
                .findFirst().orElse(nickname);
    }
}