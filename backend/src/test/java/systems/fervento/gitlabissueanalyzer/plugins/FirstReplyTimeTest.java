/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.plugins;

import com.google.common.collect.ImmutableMap;
import io.swagger.model.AnalysisRequest;
import io.swagger.model.AnaylsisResult;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import systems.fervento.gitlabissueanalyzer.plugins.utils.HistogramBuilder.Histogram;

/**
 *
 * @author nonplay
 */
public class FirstReplyTimeTest {
    
    @BeforeClass
    public static void beforeAll() {
        instance = new FirstReplyTime();
    }
    
    private static FirstReplyTime instance;
    
    @Test
    public void testAnalyzeIssues() throws IOException {
        AnalysisRequest analysisRequest = new AnalysisRequest();
        analysisRequest.setName(instance.getName());
        
        String developer = "Nonplay";
        List<Issue> issueList = 
            Arrays.asList(
                PluginTestUtils.buildIssue(1, "01/01/2011 10:00", null, null),
                PluginTestUtils.buildIssue(2, "01/01/2011 10:00", null, null),
                PluginTestUtils.buildIssue(3, "01/01/2011 10:00", null, null),
                PluginTestUtils.buildIssue(4, "01/01/2011 10:00", null, null)
            );
        Map<Integer, List<Comment>> commentListIssueNumberMap =
            (Map)ImmutableMap.builder()
                .put(1, Arrays.asList(PluginTestUtils.buildComment("01/01/2011 11:00", developer), PluginTestUtils.buildComment("02/01/2011 10:30", developer)))
                .put(2, Arrays.asList(PluginTestUtils.buildComment("01/01/2011 11:00", developer)))
                .put(3, Arrays.asList(PluginTestUtils.buildComment("01/01/2011 14:30", developer)))
            .build();
        
        AnaylsisResult anaylsisResult 
                = instance.analyzeIssues(analysisRequest, PluginTestUtils.buildIssueFetcherMock(issueList, commentListIssueNumberMap), 
                                            PluginTestUtils.getGitHubMockedUser(), PluginTestUtils.getGitHubMockedRepo());
        
        // {histogram={"labels":["1,000000-1,000000","4,500000-4,500000"],"series":[2,1]}, average=2.166666666666667, variance=4.083333333333334, name=FirstReplyTime}
        System.out.println(anaylsisResult);
        
        double expectedMean = (1+1+4.5)/3.0;
        double expectedVariance = new Variance().evaluate(new double[] { 1, 1, 4.5 });

        Assert.assertEquals(instance.getName(), anaylsisResult.getName());        
        Assert.assertEquals(expectedMean, Double.parseDouble(anaylsisResult.get(FirstReplyTime.KEY_OUT_AVERAGE_TIME)), 1e-4);
        Assert.assertEquals(expectedVariance, Double.parseDouble(anaylsisResult.get(FirstReplyTime.KEY_OUT_VARIANCE)), 1e-4);
        
        Histogram actualHistogram = PluginTestUtils.deserializeJsonFromMap(anaylsisResult, FirstReplyTime.KEY_OUT_HISTOGRAM, Histogram.class);
        Assert.assertEquals(Arrays.asList("1,000000-1,000000","4,500000-4,500000"), actualHistogram.getLabels());
        Assert.assertEquals(Arrays.asList(2L, 1L), actualHistogram.getSeries());
        
    }
    
}
