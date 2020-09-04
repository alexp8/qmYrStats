package org.cncnet.yrqm.graphs;

import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.PieChart;
import com.googlecode.charts4j.Slice;
import org.cncnet.yrqm.model.YRCompiledReport;
import org.cncnet.yrqm.model.reports.YRAlliedVsYuriReport;
import org.cncnet.yrqm.model.reports.YRSovVsAlliedReport;
import org.cncnet.yrqm.model.reports.YRSovVsYuriReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.googlecode.charts4j.Color.BLACK;

public class YRGraphGenerator {

    private static final Logger logger = LogManager.getLogger(YRGraphGenerator.class.getName());

    public static void createPieChartsFromMatchupsPerMap(List<YRCompiledReport> yrCompiledReports) {

        for (YRCompiledReport yrCompiledReport : yrCompiledReports) { //generate a pie graph for each map report

            Slice s1;
            Slice s2;
            String title;

            if (yrCompiledReport instanceof YRAlliedVsYuriReport) {

                YRAlliedVsYuriReport yrAlliedVsYuriReport = (YRAlliedVsYuriReport) yrCompiledReport;

                int alliedWins = yrAlliedVsYuriReport.getAlliedWins();
                int yuriWins = yrAlliedVsYuriReport.getYuriWins();
                double totalWins = alliedWins + yuriWins;

                int yuriPercent = (int) (((double) yuriWins) / totalWins * 100);
                int alliedPercent = (int) (((double) alliedWins) / totalWins * 100);

                s1 = Slice.newSlice(alliedPercent, Color.newColor(Color.BLUE.toString()), alliedWins + " allied wins " + alliedPercent + "percent");
                s2 = Slice.newSlice(yuriPercent, Color.newColor(Color.PURPLE.toString()), yuriWins + " yuri wins " + yuriPercent + " percent");

                title = yrAlliedVsYuriReport.getMapName() + " Allied vs Yuri";

            } else if (yrCompiledReport instanceof YRSovVsAlliedReport) {

                YRSovVsAlliedReport sovVsAlliedReport = (YRSovVsAlliedReport) yrCompiledReport;

                int alliedWins = sovVsAlliedReport.getAlliedWins();
                int sovWins = sovVsAlliedReport.getSovWins();
                double totalWins = alliedWins + sovWins;

                int sovietPercent = (int) (((double) sovWins) / totalWins * 100);
                int alliedPercent = (int) (((double) alliedWins) / totalWins * 100);

                s1 = Slice.newSlice(alliedPercent, Color.newColor(Color.BLUE.toString()), "Allied", alliedWins + " allied wins " + alliedPercent + " percent");
                s2 = Slice.newSlice(sovietPercent, Color.newColor(Color.RED.toString()), "Soviet", sovWins + " sov wins " + sovietPercent + " percent");

                title = sovVsAlliedReport.getMapName() + " Allied vs Soviet";

            } else {
                YRSovVsYuriReport yrSovVsYuriReport = (YRSovVsYuriReport) yrCompiledReport;

                int sovWins = yrSovVsYuriReport.getSovWins();
                int yuriWins = yrSovVsYuriReport.getYuriWins();
                double totalWins = sovWins + yuriWins;

                int sovietPercent = (int) (((double) sovWins) / totalWins * 100);
                int yuriPercent = (int) (((double) yuriWins) / totalWins * 100);

                s1 = Slice.newSlice(sovietPercent, Color.newColor(Color.BLUE.toString()), "Soviet", sovWins + " sov wins " + sovietPercent + "percent");
                s2 = Slice.newSlice(yuriPercent, Color.newColor(Color.PURPLE.toString()), "Yuri", yuriWins + " yuri wins " + yuriPercent + "percent");

                title = yrSovVsYuriReport.getMapName() + " Soviet vs Yuri";
            }

            PieChart chart = GCharts.newPieChart(s1, s2);
            chart.setTitle(title, BLACK, 16);
            chart.setSize(600, 400);

            String url = chart.toURLString();
            System.out.println(title + ": " + url);
        }
    }
}