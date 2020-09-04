package org.cncnet.yrqm.runner;

import org.cncnet.yrqm.model.QMReport;
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

        String filename = args[0];

        Path path = Paths.get("src/main/resources/" + filename);

        QMReportParser qmReportParser = new QMReportParser();
//        qmReportParser.parseYRReportsByMatchup(Files.newBufferedReader(file));

//        qmReportParser.parseYRReportsByMap(Files.newBufferedReader(file));

        QMReport.YRReport[] yrReports_filtered = QMPlayerFilter.filterPlayers(Files.newBufferedReader(path), 10);

        QMElo.generateElo(yrReports_filtered, 1000);
    }
}