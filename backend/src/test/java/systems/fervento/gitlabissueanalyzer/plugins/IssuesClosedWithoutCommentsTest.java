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
import java.util.stream.Collectors;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.BeforeClass;

/**
 *
 * @author nonplay
 */
public class IssuesClosedWithoutCommentsTest {
@BeforeClass
    public static void beforeAll() {
        instance = new IssuesClosedWithoutComments();
    }
    
    private static IssuesClosedWithoutComments instance;
    
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
            .build();
        
        AnaylsisResult anaylsisResult 
                = instance.analyzeIssues(analysisRequest, PluginTestUtils.buildIssueFetcherMock(issueList, commentListIssueNumberMap), 
                                            PluginTestUtils.getGitHubMockedUser(), PluginTestUtils.getGitHubMockedRepo());
        
        // {notCommentedIssues=[{"id":2,"createdAt":"Jan 1, 2011 10:00:00 AM","comments":0,"number":3,"state":"open"},{"id":3,"createdAt":"Jan 1, 2011 10:00:00 AM","comments":0,"number":4,"state":"open"}], totalNotCommentedIssues=2, name=IssuesClosedWithoutComments}
        System.out.println(anaylsisResult);
        
        Assert.assertEquals(instance.getName(), anaylsisResult.getName());        
        Assert.assertEquals(2, Integer.parseInt(anaylsisResult.get(IssuesClosedWithoutComments.KEY_OUT_TOTAL_NOT_COMMENTED_ISSUES)));
        List<Map<String, Object>> actualIssueList = 
            (List<Map<String, Object>>)
                (PluginTestUtils.deserializeJsonFromMap(anaylsisResult, IssuesClosedWithoutComments.KEY_OUT_NOT_COMMENTED_ISSUES, List.class));
        
        Assert.assertEquals(3, ((Double)actualIssueList.get(0).get("number")).intValue());
        Assert.assertEquals(4, ((Double)actualIssueList.get(1).get("number")).intValue());
    }
}
