package jupai9.examples;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.knowm.xchart.*;
//import org.knowm.xchart.demo.charts.ExampleChart;
import org.knowm.xchart.style.Styler.LegendPosition;

/**
 * Basic Bar Chart
 *
 * <p>Demonstrates the following:
 *
 * <ul>
 *   <li>Integer categories as List
 *   <li>All positive values
 *   <li>Single series
 *   <li>Place legend at Inside-NW position
 *   <li>Bar Chart Annotations
 */
public class BarChart01 {

    public static void main(String[] args) {

        //ExampleChart<CategoryChart> exampleChart = new BarChart01();
        //CategoryChart chart = exampleChart.getChart();
        //new SwingWrapper<>(chart).displayChart();



        //BarChart01.getChart(names, num);
    }
    public static void getChart(List<String> A, List<Long> B,String title ,String x ,String y) {

        // Create Chart
        CategoryChart chart =
                new CategoryChartBuilder()
                        .width(800)
                        .height(600)
                        .title(title)
                        .xAxisTitle(x)
                        .yAxisTitle(y)
                        .build();

        // Customize Chart
        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        //chart.getStyler().setLabelsVisible(false);
        chart.getStyler().setPlotGridLinesVisible(false);

        // Series
        chart.addSeries("JobFreq", A, B);
//        for (int i = 0; i < A.size(); i++) {
//            chart.addSeries(A.get(i), B.get(i));
//        }

        new SwingWrapper<>(chart).displayChart();
    }

}
