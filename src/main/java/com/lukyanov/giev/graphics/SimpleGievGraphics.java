package com.lukyanov.giev.graphics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.lukyanov.giev.algorithm.SimpleGiev1;

import lombok.Getter;
import lombok.Setter;



public class SimpleGievGraphics {

   private XYSeriesCollection dataset;
   private ChartPanel chartPanel;

   @Getter
   @Setter
   private boolean state = false;

   @Getter
   @Setter
   private List<XYSeries> seriesList = new ArrayList<>();

   @Getter
   @Setter
   private List<Double> averageList = new ArrayList<>();

   @Getter
   @Setter
   private List<Double> maxList = new ArrayList<>();

   public SimpleGievGraphics(XYSeriesCollection dataset, final ChartPanel chartPanel) {
      this.dataset = dataset;
      this.chartPanel = chartPanel;
   }

   public void run(final int generationCount, final int populationSize, final int genesCount, final double crossingOverP,
         final double mutationP,
         final double from, final double to) {

      SimpleGiev1 simpleGiev = SimpleGiev1.builder()
            .generationCount(generationCount)
            .genesCount(genesCount)
            .populationSize(populationSize)
            .crossingOverP(crossingOverP)
            .mutationP(mutationP)
            .from(from)
            .to(to)
            .build();


      while (simpleGiev.hasNext()) {
         List<Pair<Double, Double>> population = simpleGiev.generateNextPopulation();

         XYSeries chromosomes = new XYSeries("chromosomes~" + simpleGiev.getCurrentPopulationNumber());


         for (Pair<Double, Double> pair : population) {
            chromosomes.add(pair.getKey(), pair.getValue());
         }

         seriesList.add(chromosomes);
         averageList.add(simpleGiev.getAverage());
         maxList.add(simpleGiev.getMax());
      }
      state = true;
   }
}
