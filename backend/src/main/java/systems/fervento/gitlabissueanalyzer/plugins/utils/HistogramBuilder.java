/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.plugins.utils;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 *
 * @author nonplay
 */
public class HistogramBuilder {
    private final int numberOfBins;
    private final List<Double> values = new ArrayList<>();
    private EmpiricalDistribution distribution;

    public HistogramBuilder(int numberOfBins) {
        this.numberOfBins = numberOfBins;
    }
    
    public void insertValue(double value) {
        values.add(value);
    }
    
    public void compute() {
        distribution = new EmpiricalDistribution(numberOfBins);
        distribution.load(values.stream().mapToDouble(Double::doubleValue).toArray());
    }
    
    public double getMin() {
        assertDistributionComputed();
        
        return distribution.getSupportLowerBound();
    }
    
    public double getMax() {
        assertDistributionComputed();
        
        return distribution.getSupportUpperBound();
    }
    
    public Histogram buildHistogram() {        
        assertDistributionComputed();
        
        Histogram histogram = new Histogram(values.size()); 
        List<org.apache.commons.math3.stat.descriptive.SummaryStatistics> binStats = distribution.getBinStats();
        for (int i = 0; i < binStats.size(); i++) {
            SummaryStatistics bin = binStats.get(i);
            double currentMin = bin.getMin();
            double currentMax = bin.getMax();
            long numberOfSamples = bin.getN();
            
            if (!Double.isNaN(currentMin) && !Double.isNaN(currentMax)) {
                histogram.labels.add(String.format("%f-%f", currentMin, currentMax));
                histogram.series.add(numberOfSamples);
            }
        }

        return histogram;
    }
    
    private void assertDistributionComputed() {
        if (distribution == null) {
            throw new IllegalStateException("You must call compute() first!");
        }
    }
    
    public double getAverage() {
        assertDistributionComputed();
        
        return distribution.getNumericalMean();
    } 
    
    public double getVariance() {
        assertDistributionComputed();
        
        return distribution.getNumericalVariance();
    }
    
    public final static class Histogram {
        private final List<String> labels;
        private final List<Long> series;

        public Histogram(int samples) {
            labels = new ArrayList<>(samples);
            series = new ArrayList<>(samples);
        }
        
        public List<String> getLabels() {
            return labels;
        }

        public List<Long> getSeries() {
            return series;
        }

        @Override
        public String toString() {
            
            return "Histogram{" + "labels=[" + String.join(",", labels) + "], series=[" + Arrays.toString(series.toArray()) + "]}";
        }
        
        
        
    }
    
}
