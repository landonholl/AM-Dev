package com.birdsprime.aggressivemobs;

import java.util.Random;

public class RNG {

	//Generate a random number between Min (inclusive) and Max (inclusive)
	public RNG()
	{		

	}
	public int GetInt(int Min, int Max) {
		//Get random number for property weights
		Random New_RNG = new Random();
		//return New_RNG.nextInt(Min, Max);		//1.19.3
		return (int)GetDouble((double)Min, (double)Max);
	}
	
	//Get random double-precision decimal value
	public double GetDouble(double Min, double Max)
	{
		Random New_RNG = new Random();
		double Weights_Diff = (Max - Min);
		double Weights_Pct = Weights_Diff / 100.0;
		double Rand_PctInc = New_RNG.nextDouble();
		double Final_Val = Min + (Weights_Diff * Rand_PctInc);
		return Final_Val;		//1.20.1
		//return New_RNG.nextDouble(Min, Max); //1.19.3
	}
	
}
