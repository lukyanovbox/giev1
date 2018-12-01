package com.lukyanov.giev.util;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.lang3.tuple.Pair;

import com.lukyanov.giev.algorithm.Gene;

import static com.lukyanov.giev.algorithm.Gene.ONE;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.StrictMath.sin;


public class CoordinatesExecutor {

   public static final ToDoubleFunction<Double> mathFunctionExecutor = t -> (t + 1.3) * sin(0.5 * PI * t + 1);

   public static final Function<List<Gene>, Double> chromosomeToDoubleConverter = genes -> {
      int i = genes.size() - 1;
      double r = 0;
      for (Gene gene : genes) {
         if (gene == ONE) {
            r += pow(2, i);
         }
         i--;
      }
      return r;
   };

   public static Function<Double, Double> rToXCordinateConverter(final double from, final double to, int genesCount) {
      return r -> from + r * (to - from) / (pow(2, genesCount) - 1);
   }


   public static List<Pair<Double, Double>> generateXYPairs(final double from, final double to, final double step) {

      return DoubleStream
            .iterate(from, n -> n + step)
            .limit((int) ((to - from) / step + 1))
            .mapToObj(t -> Pair.of(t, mathFunctionExecutor.applyAsDouble(t)))
            .collect(Collectors.toList());
   }





}
