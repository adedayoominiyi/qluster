// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.aggregate;

/**
 * An aggregator that sums doubles
 * @author Mario Fusco
 */
public class SumDouble extends InitializedPairAggregator<Double> {

    public SumDouble(Double firstItem) {
        super(firstItem);
    }

    /**
     * Aggregates two doubles by summing them
     * @param first The first double to be summed
     * @param second The second double to be summed
     * @return The sum of the two doubles
     */
	public Double aggregate(Double first, Double second) {
		return first + second;
	}
}
