package org.cncnet.yrqm.runner;

import org.cncnet.yrqm.model.QMReport;
import org.cncnet.yrqm.model.YRCompiledReport;
import org.cncnet.yrqm.parser.QMElo;
import org.cncnet.yrqm.parser.QMPlayerFilter;
import org.cncnet.yrqm.parser.QMReportParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QMReportMain {

    private QMReportMain() {

    }

    public static void main(String... args) throws IOException {

        Path[] paths = new Path[]{Paths.get("src/main/resources/5-2021-bulk.json")};

        QMReportParser qmReportParser = new QMReportParser();

        QMReport.YRReport[] yrReports_filtered = QMPlayerFilter.filterPlayers(paths, 15); //filter out games that disconnected, and any games played by player with less than 15 total played games

        qmReportParser.parseYRReportsByMatchup(yrReports_filtered); //calculate how many games were played by each faction

        qmReportParser.generateTotalGamesPlayedPerMap(yrReports_filtered); //calculate how many maps played on each map

        qmReportParser.parseYRReportsByMap(yrReports_filtered); //calculate  the wins by each side per map

        QMElo.generateElo(yrReports_filtered, 1000); //generate ELO ratings for each faction on each map
    }
}