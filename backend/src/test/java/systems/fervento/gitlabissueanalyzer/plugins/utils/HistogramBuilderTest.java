/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.plugins.utils;

import java.util.Arrays;
import org.junit.Assert;
import systems.fervento.gitlabissueanalyzer.plugins.utils.HistogramBuilder.Histogram;

/**
 *
 * @author nonplay
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Swagger2SpringBoot.class)
//@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class HistogramBuilderTest {
    
    public HistogramBuilderTest() {
    }

    @org.junit.Test
    public void testSomeMethod() {
        int NUMBER_OF_BUCKETS = 3;
        HistogramBuilder histogramBuilder = new HistogramBuilder(NUMBER_OF_BUCKETS);
        
        for (int i = 1; i <= 10; i++) {
            for (int j = 0; j < i; j++) {
                histogramBuilder.insertValue(i);
            }
        }
        histogramBuilder.compute();
        
        Assert.assertEquals(1.0, histogramBuilder.getMin(), 1e-6);
        Assert.assertEquals(10.0, histogramBuilder.getMax(), 1e-6);
        
        Histogram histogram = histogramBuilder.buildHistogram();
        Assert.assertEquals(
                Arrays.asList("1,000000-4,000000","5,000000-7,000000","8,000000-10,000000"), 
                histogram.getLabels());
        
        Assert.assertEquals(
                Arrays.asList(1L + 2L + 3L + 4L  , 
                                5L + 6L + 7L,
                                8L + 9L + 10L), 
                histogram.getSeries());
        
    }
    
}
