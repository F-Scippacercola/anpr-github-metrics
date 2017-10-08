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
import org.junit.Test;
import org.junit.BeforeClass;
import systems.fervento.gitlabissueanalyzer.plugins.utils.HistogramBuilder;

/**
 *
 * @author nonplay
 */
public class TicketTimeSeriesTest {
    
    @BeforeClass
    public static void beforeAll() {
        instance = new TicketTimeSeries();
    }
    
    private static TicketTimeSeries instance;
    
    @Test
    public void testAnalyzeIssues() throws IOException {
        AnalysisRequest analysisRequest = new AnalysisRequest();
        analysisRequest.setName(instance.getName());
        
        String developer = "Nonplay";
        List<Issue> issueList = 
            Arrays.asList(
                PluginTestUtils.buildIssue(1, "01/01/2011 10:00", "01/01/2011 11:00", null),
                PluginTestUtils.buildIssue(2, "01/01/2011 10:00", "01/01/2011 11:00", null),
                PluginTestUtils.buildIssue(3, "02/01/2011 10:00", "03/01/2011 14:30", null),
                PluginTestUtils.buildIssue(4, "04/01/2011 10:00", null, null)
            );
        Map<Integer, List<Comment>> commentListIssueNumberMap = (Map)ImmutableMap.builder().build();
        
        AnaylsisResult anaylsisResult 
                = instance.analyzeIssues(analysisRequest, PluginTestUtils.buildIssueFetcherMock(issueList, commentListIssueNumberMap), 
                                            PluginTestUtils.getGitHubMockedUser(), PluginTestUtils.getGitHubMockedRepo());
        
        // {timeRangeDays=4, plot={"labels":["01/01/11","02/01/11","03/01/11","04/01/11"],"series":[[2,3,3,4],[2,2,3,3]]}, name=TicketTimeSeries}
        System.out.println(anaylsisResult);

        Assert.assertEquals(instance.getName(), anaylsisResult.getName());        
        Assert.assertEquals(4, Integer.parseInt(anaylsisResult.get(TicketTimeSeries.KEY_OUT_TIME_RANGE_DAYS)));
        Map<String, Object> plotDescription = PluginTestUtils.deserializeJsonFromMap(anaylsisResult, TicketTimeSeries.KEY_OUT_PLOT, Map.class);
        
        Assert.assertEquals(Arrays.asList("01/01/11","02/01/11","03/01/11","04/01/11"), plotDescription.get(TicketTimeSeries.KEY_OUT_LABELS));
        Assert.assertEquals(Arrays.asList(2.0, 3.0, 3.0, 4.0), ((List)plotDescription.get(TicketTimeSeries.KEY_OUT_SERIES)).get(0));
        Assert.assertEquals(Arrays.asList(2.0, 2.0, 3.0, 3.0), ((List)plotDescription.get(TicketTimeSeries.KEY_OUT_SERIES)).get(1));
    }
    
}
