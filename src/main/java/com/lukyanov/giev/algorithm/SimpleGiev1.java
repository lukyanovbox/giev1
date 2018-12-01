package com.lukyanov.giev.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

import static com.lukyanov.giev.algorithm.Gene.ONE;
import static com.lukyanov.giev.algorithm.Gene.ZERO;
import static com.lukyanov.giev.util.CoordinatesExecutor.chromosomeToDoubleConverter;
import static com.lukyanov.giev.util.CoordinatesExecutor.mathFunctionExecutor;
import static com.lukyanov.giev.util.CoordinatesExecutor.rToXCordinateConverter;
import static java.lang.Math.abs;
import static java.util.stream.Collectors.collectingAndThen;
import lombok.Builder;
import lombok.Getter;


@Builder
public class SimpleGiev1 {

   final int populationSize;
   int genesCount;
   final int generationCount;

   final double crossingOverP;
   final double mutationP;

   double from;
   double to;

   @Getter
   volatile int currentPopulationNumber = 0;
   List<Chromosome> population;


   final Function<List<Gene>, Chromosome> chromosomeFinisher = genes ->
         Chromosome.builder()
               .genes(genes)
               .toDoubleConverter(chromosomeToDoubleConverter)
               .toXCordinateConverter(rToXCordinateConverter(from, to, genesCount))
               .build();

   final Supplier<Chromosome> chromosomeGenerator = () ->
   {
      //      List<Gene> genes = new ArrayList<>();
      //      int x_ = RandomUtils.nextInt(0, (int) Math.pow(2, genesCount) - 1);
      //      System.out.println(x_);
      //      Arrays.stream(Integer.toBinaryString(x_).split("")).map(
      //            s -> s.equals("1") ? ONE : ZERO).forEach(genes::add);
      //
      //      for (int i = genes.size(); i < genesCount; i++) {
      //         genes.add(0,ZERO);
      //      }
      //
      //      return chromosomeFinisher.apply(genes);
      return IntStream.rangeClosed(1, genesCount)
            .boxed()
            .map(i -> RandomUtils.nextInt(0, 2))
            .map(i -> i == 0 ? ZERO : ONE)
            .collect(collectingAndThen(Collectors.toList(), chromosomeFinisher));
   };


   public Double getMax() {
      return population.stream()
            .map(Chromosome::getX)
            .mapToDouble(mathFunctionExecutor)
            .max()
            .getAsDouble();
   }

   public Double getAverage() {
      return population.stream()
            .map(Chromosome::getX)
            .mapToDouble(mathFunctionExecutor)
            .average()
            .getAsDouble();
   }


   public boolean hasNext() {
      return currentPopulationNumber < generationCount;
   }


   public List<Pair<Double, Double>> generateNextPopulation() {
      if (currentPopulationNumber == 0) {
         population = Stream.generate(chromosomeGenerator)
               .limit(populationSize)
               .collect(Collectors.toList());

         System.out.println(population.stream()
               .map(ch -> ch.getGenes().stream().map(g -> g == ZERO ? "0" : "1").collect(Collectors.joining(""))).collect(
                     Collectors.joining("|\n ")));

         System.out.println(population.stream()
               .map(ch -> ch.getX().toString()).collect(
                     Collectors.joining("|\n ")));
      }
      else {
         population = evolutionPopulation(population);
      }
      currentPopulationNumber++;

      return population.stream()
            .map(chr -> Pair.of(chr.getX(), mathFunctionExecutor.applyAsDouble(chr.getX())))
            .collect(Collectors.toList());
   }





   private List<Chromosome> evolutionPopulation(List<Chromosome> population) {

      List<Chromosome> intermediatePopulation = generateIntermediatePopulation(population);
      List<Chromosome> newPopulation = selectAndCrossingOver(intermediatePopulation);

      return mutate(newPopulation);
   }

   private List<Chromosome> generateIntermediatePopulation(List<Chromosome> population) {
      final Double minFValue = population.stream()
            .map(Chromosome::getX)
            .mapToDouble(mathFunctionExecutor)
            .min()
            .getAsDouble();

      final Double correctCoeff = abs(minFValue);

      final Double sumFcVal = population.stream()
            .mapToDouble(chr -> mathFunctionExecutor.applyAsDouble(chr.getX()) + correctCoeff)
            .sum();

      Map<Chromosome, Double> pChromosomeMap = population.stream()
            .map(chr -> Pair.of(chr, (mathFunctionExecutor.applyAsDouble(chr.getX()) + correctCoeff) / sumFcVal))
            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

      double var = 0;
      Map<Double, Chromosome> rouletteMap = new HashMap<>();

      for (Map.Entry<Chromosome, Double> entry : pChromosomeMap.entrySet()) {
         var += entry.getValue();
         rouletteMap.put(var, entry.getKey());
      }



      List<Chromosome> intermediateChromosomeList = new ArrayList<>();
      for (int i = 0; i < rouletteMap.keySet().size(); i++) {
         double rnd = RandomUtils.nextDouble(0,
               rouletteMap.keySet().stream().max(Comparator.comparing(Double::doubleValue)).get());
         for (Double key : rouletteMap.keySet().stream().sorted().collect(Collectors.toList())) {
            if (rnd < key) {
               intermediateChromosomeList.add(rouletteMap.get(key));
               break;
            }
         }
      }

      return intermediateChromosomeList;
   }


   private List<Chromosome> selectAndCrossingOver(List<Chromosome> intermediatePopulation) {
      List<Chromosome> newPopulation = new ArrayList<>();
      for (int i = 0; i < intermediatePopulation.size() / 2; i++) {
         int firstItemIndex = RandomUtils.nextInt(0, intermediatePopulation.size());
         int secondItemIndex = firstItemIndex;
         while (secondItemIndex == firstItemIndex) {
            secondItemIndex = RandomUtils.nextInt(0, intermediatePopulation.size());
         }
         if (RandomUtils.nextDouble(0, 1) < crossingOverP) {
            newPopulation.addAll(
                  crossOver(intermediatePopulation.get(firstItemIndex), intermediatePopulation.get(secondItemIndex)));
         }
         else {
            newPopulation.add(chromosomeFinisher.apply(new ArrayList<>(intermediatePopulation.get(firstItemIndex).getGenes())));
            newPopulation.add(chromosomeFinisher.apply(new ArrayList<>(intermediatePopulation.get(secondItemIndex).getGenes())));
         }
      }

      if (intermediatePopulation.size() % 2 != 0) {
         newPopulation.add(chromosomeFinisher.apply(
               new ArrayList<>(intermediatePopulation.get(RandomUtils.nextInt(0, intermediatePopulation.size())).getGenes())));
      }

      return newPopulation;
   }


   private List<Chromosome> crossOver(Chromosome first, Chromosome second) {
      List<Gene> newFirstItemGenes = new ArrayList<>();
      List<Gene> newSecondItemGenes = new ArrayList<>();

      List<Gene> firstGrayGenes = binaryToGray(first.getGenes());
      List<Gene> secondGrayGenes = binaryToGray(second.getGenes());

      int crossIndex = RandomUtils.nextInt(0, first.getGenes().size() - 1);

      for (int i = 0; i <= crossIndex; i++) {
         newFirstItemGenes.add(firstGrayGenes.get(i));
         newSecondItemGenes.add(secondGrayGenes.get(i));
      }
      for (int i = crossIndex + 1; i < first.getGenes().size(); i++) {
         newFirstItemGenes.add(secondGrayGenes.get(i));
         newSecondItemGenes.add(firstGrayGenes.get(i));
      }

      return ImmutableList.of(
            chromosomeFinisher.apply(grayToBinary(newFirstItemGenes)),
            chromosomeFinisher.apply(grayToBinary(newSecondItemGenes))
      );
   }


   private List<Chromosome> mutate(List<Chromosome> population) {
      for (Chromosome chromosome : population) {
         double rnd = RandomUtils.nextDouble(0, 1);

         if (rnd < mutationP) {
            int rndIndex = RandomUtils.nextInt(0, chromosome.getGenes().size());

            chromosome.getGenes().set(rndIndex, chromosome.getGenes().get(rndIndex) == ZERO ? ONE : ZERO);
         }
      }

      return population;
   }

   private List<Gene> binaryToGray(List<Gene> binaryGenes) {
      List<Gene> grayGenes = new ArrayList<>();
      grayGenes.add(binaryGenes.get(0));
      for (int i = 1; i < binaryGenes.size(); i++) {
         Gene g_1 = binaryGenes.get(i - 1);
         Gene g = binaryGenes.get(i);
         if ((g_1 == ONE && g == ZERO) || (g_1 == ZERO && g == ONE)) {
            grayGenes.add(ONE);
         }
         else {
            grayGenes.add(ZERO);
         }
      }

      return grayGenes;
   }

   private List<Gene> grayToBinary(List<Gene> grayGenes) {
      List<Gene> binaryGenes = new ArrayList<>();
      Gene value = grayGenes.get(0);
      binaryGenes.add(value);
      for (int i = 1; i < grayGenes.size(); i++) {
         if (grayGenes.get(i) == ONE) {
            value = value == ONE ? ZERO : ONE;
         }
         binaryGenes.add(value);
      }

      return binaryGenes;
   }
}
