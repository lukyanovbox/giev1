package com.lukyanov.giev.algorithm;

import java.util.List;
import java.util.function.Function;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@Builder
public class Chromosome {
   private final Function<List<Gene>, Double> toDoubleConverter;
   private final Function<Double, Double> toXCordinateConverter;

   @Getter
   private final List<Gene> genes;


   public Double getR() {
      return toDoubleConverter.apply(genes);
   }

   public Double getX() {
      return toDoubleConverter.andThen(toXCordinateConverter)
            .apply(genes);
   }
}
