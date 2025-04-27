package com.birdsprime.aggressivemobs;

import java.util.Random;

public class RNG {

    private static final Random RAND = new Random();

    private RNG() {} // no instantiation

    public static int GetInt(int Min, int Max) {
        return Min + RAND.nextInt((Max - Min) + 1);
    }

    public static double GetDouble(double Min, double Max) {
        return Min + (Max - Min) * RAND.nextDouble();
    }
}
