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
public class TicketGeneralTest {
    
    @BeforeClass
    public static void beforeAll() {
        instance = new TicketGeneral();
    }
    
    private static TicketGeneral instance;
    
    @Test
    public void testAnalyzeIssues() throws IOException {
        AnalysisRequest analysisRequest = new AnalysisRequest();
        analysisRequest.setName(instance.getName());
        
        String developer = "Nonplay";
        List<Issue> issueList = 
            Arrays.asList(
                PluginTestUtils.buildIssue(1, "01/01/2011 10:00", "01/01/2011 11:00", null),
                PluginTestUtils.buildIssue(2, "01/01/2011 10:00", "01/01/2011 11:00", null),
                PluginTestUtils.buildIssue(3, "01/01/2011 10:00", "01/01/2011 14:30", null),
                PluginTestUtils.buildIssue(4, "01/01/2011 10:00", null, null)
            );
        Map<Integer, List<Comment>> commentListIssueNumberMap = (Map)ImmutableMap.builder().build();
        
        AnaylsisResult anaylsisResult 
                = instance.analyzeIssues(analysisRequest, PluginTestUtils.buildIssueFetcherMock(issueList, commentListIssueNumberMap), 
                                            PluginTestUtils.getGitHubMockedUser(), PluginTestUtils.getGitHubMockedRepo());
        
        // {histogram={"labels":["1,000000-1,000000","4,500000-4,500000"],"series":[2,1]}, average=2.166666666666667, variance=4.083333333333334, name=FirstReplyTime}
        System.out.println(anaylsisResult);
        
        Assert.assertEquals(instance.getName(), anaylsisResult.getName());        
        Assert.assertEquals(1, Integer.parseInt(anaylsisResult.get(TicketGeneral.KEY_OUT_OPEN_TICKETS)));
        Assert.assertEquals(3, Integer.parseInt(anaylsisResult.get(TicketGeneral.KEY_OUT_CLOSED_TICKETS)));
        
    }
}
