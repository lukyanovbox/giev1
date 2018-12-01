package com.lukyanov.giev.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import static com.lukyanov.giev.algorithm.Gene.ONE;
import static com.lukyanov.giev.algorithm.Gene.ZERO;
import static com.lukyanov.giev.util.CoordinatesExecutor.chromosomeToDoubleConverter;


class CoordinatesExecutorTest {

   @BeforeEach
   void setUp() {
   }

   @AfterEach
   void tearDown() {
   }

   @Test
   void rToXCordinateConverter7349() {

      double x = chromosomeToDoubleConverter.apply(
            ImmutableList.of(ONE, ONE, ONE, ZERO, ZERO, ONE, ZERO, ONE, ONE, ZERO, ONE, ZERO, ONE));

      Assertions.assertEquals(7349, x);
   }

   @Test
   void rToXCordinateConverter5() {

      double x = chromosomeToDoubleConverter.apply(
            ImmutableList.of(ONE, ONE, ONE, ZERO, ONE));

      Assertions.assertEquals(29, x);
   }
}