package com.lukyanov.giev;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

import org.apache.commons.lang3.tuple.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.lukyanov.giev.graphics.SimpleGievGraphics;
import com.lukyanov.giev.util.CoordinatesExecutor;


public class Application {

   private static final String TITLE = "f(t)=(t+1.3)sin(0.5*pi*t+1), t \u2208[0,7]";
   private static final int FROM = 0;
   private static final int TO = 7;

   private JButton runAlgorithm;
   private JPanel panelMain;
   private JTextField populationSize;
   private JTextField genesCount;
   private JTextField crossingOverP;
   private JTextField mutationP;
   private JTextField generationsCount;
   private JPanel chartPanelWrapper;
   private JLabel averageLabel;
   private JLabel maxLabel;

   private XYSeriesCollection dataset;

   private SimpleGievGraphics simpleGievGraphics;


   public Application() {
      runAlgorithm.addActionListener(e -> {
         simpleGievGraphics
               .run(Integer.valueOf(generationsCount.getText()),
                     Integer.valueOf(populationSize.getText()),
                     Integer.valueOf(genesCount.getText()),
                     Double.valueOf(crossingOverP.getText()),
                     Double.valueOf(mutationP.getText()),
                     FROM,
                     TO);
      });
   }


   public static void main(String[] args) throws InterruptedException {
      JFrame frame = new JFrame("Giev1");
      Application app = new Application();

      frame.setContentPane(app.panelMain);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);


      while (true) {
         if (app.simpleGievGraphics.isState()) {
            int i = 0;
            for (XYSeries series : app.simpleGievGraphics.getSeriesList()) {
               if (app.dataset.getSeriesCount() > 1) {
                  app.dataset.removeSeries(1);
               }
               app.dataset.addSeries(series);

               app.averageLabel.setText(String.valueOf(app.simpleGievGraphics.getAverageList().get(i)));
               app.maxLabel.setText(String.valueOf(app.simpleGievGraphics.getMaxList().get(i)));
               i++;
               Thread.sleep(1000);
            }
            app.simpleGievGraphics.setState(false);
            app.simpleGievGraphics.setSeriesList(new ArrayList<>());
            app.simpleGievGraphics.setAverageList(new ArrayList<>());
            app.simpleGievGraphics.setMaxList(new ArrayList<>());
         }
         else {
            Thread.sleep(1000);
         }
      }
   }

   private XYSeriesCollection createDataset() {

      XYSeries series = new XYSeries(TITLE);
      java.util.List<Pair<Double, Double>> pairs = CoordinatesExecutor.generateXYPairs(FROM, TO, 0.01);

      for (Pair<Double, Double> pair : pairs) {
         series.add(pair.getLeft(), pair.getRight());
      }

      XYSeriesCollection dataset = new XYSeriesCollection();
      dataset.addSeries(series);


      //      XYSeries chromosomes = new XYSeries("chromosomes");
      //      chromosomes.add(15, 567);
      //      chromosomes.add(21, 612);
      //      chromosomes.add(27, 800);
      //      chromosomes.add(30, 980);
      //      chromosomes.add(42, 1410);
      //      chromosomes.add(50, 2350);
      //      dataset.addSeries(chromosomes);

      return dataset;
   }

   private JFreeChart createChart(XYDataset dataset) {

      JFreeChart chart = ChartFactory.createXYLineChart(
            TITLE,
            "t",
            "f(t)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
      );

      XYPlot plot = chart.getXYPlot();

      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
      renderer.setSeriesPaint(0, Color.RED);
      renderer.setSeriesStroke(0, new BasicStroke(1.0f));
      renderer.setSeriesShapesVisible(0, false);

      renderer.setSeriesPaint(1, Color.BLUE);
      renderer.setSeriesStroke(1, new BasicStroke(2.0f));
      renderer.setSeriesLinesVisible(1, false);

      plot.setRenderer(renderer);
      plot.setBackgroundPaint(Color.white);

      plot.setRangeGridlinesVisible(true);
      plot.setRangeGridlinePaint(Color.BLACK);

      plot.setDomainGridlinesVisible(true);
      plot.setDomainGridlinePaint(Color.BLACK);

      chart.getLegend().setFrame(BlockBorder.NONE);

      chart.setTitle(new TextTitle(TITLE,
                  new Font("Serif", java.awt.Font.BOLD, 18)
            )
      );

      return chart;

   }

   private void createUIComponents() {
      dataset = createDataset();

      //      dataset.seriesChanged();

      JFreeChart chart = createChart(dataset);

      ChartPanel chartPanel = new ChartPanel(chart);
      chartPanel.setChart(chart);
      chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
      chartPanel.setBackground(Color.white);
      chartPanel.setSize(300, 300);
      chartPanel.setRefreshBuffer(true);

      simpleGievGraphics = new SimpleGievGraphics(dataset, chartPanel);

      chartPanelWrapper = chartPanel;
      // TODO: place custom component creation code here
   }
}
