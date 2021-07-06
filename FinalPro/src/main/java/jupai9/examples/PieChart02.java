package jupai9.examples;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.SwingWrapper;
//import org.knowm.xchart.demo.charts.ExampleChart;

//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//import org.apache.spark.sql.DataFrameReader;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.SparkSession;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PieChart02 {

    public static void main(String[] args) {
        //Logger.getLogger("org").setLevel(Level.ERROR);

        //ExampleChart<PieChart> exampleChart = new PieChart02();
        //PieChart chart = exampleChart.getChart();
        //new SwingWrapper<PieChart>(chart).displayChart();
        java.util.List<String> names = new ArrayList<String>();
        names.add("MAAA");
        names.add("SSS");
        names.add("GGGG");
        List<Integer> num = new ArrayList<>();
        num.add(20);
        num.add(50);
        num.add(225);
        //PieChart02.getChart(names , num);
    }


    public static void getChart(List<String> A, List<Long> B) {

        // Create Chart
        PieChart chart = new PieChartBuilder().width(800).height(600).title(PieChart02.class.getSimpleName()).build();

        // Customize Chart
        Color[] sliceColors = new Color[] { new Color(224, 68, 14), new Color(230, 105, 62), new Color(236, 143, 110), new Color(243, 180, 159), new Color(246, 199, 182) };
        chart.getStyler().setSeriesColors(sliceColors);

        // Series
        for (int i = 0; i < A.size(); i++) {
            chart.addSeries(A.get(i), B.get(i));
        }

        new SwingWrapper<PieChart>(chart).displayChart();
    }
}