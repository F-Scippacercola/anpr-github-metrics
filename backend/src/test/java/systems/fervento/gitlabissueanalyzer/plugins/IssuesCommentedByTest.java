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
import systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher;
import systems.fervento.gitlabissueanalyzer.plugins.utils.HistogramBuilder;
import systems.fervento.gitlabissueanalyzer.plugins.utils.IssueStatePluginHelper;
import systems.fervento.gitlabissueanalyzer.plugins.utils.SelectionModePluginHelper;

/**
 *
 * @author nonplay
 */
public class IssuesCommentedByTest {
    
    @BeforeClass
    public static void beforeAll() throws IOException {
        instance = new IssuesCommentedBy();
        
        String developer1 = "Nonplay";
        String developer2 = "EBattista";
        List<Issue> issueList = 
            Arrays.asList(
                PluginTestUtils.buildIssue(1, "01/01/2011 10:00", "01/01/2011 11:00", null),
                PluginTestUtils.buildIssue(2, "01/01/2011 10:00", null, null),
                PluginTestUtils.buildIssue(3, "01/01/2011 10:00", null, null),
                PluginTestUtils.buildIssue(4, "01/01/2011 10:00", "01/01/2011 14:00", null)
            );
        Map<Integer, List<Comment>> commentListIssueNumberMap =
            (Map)ImmutableMap.builder()
                .put(1, Arrays.asList(PluginTestUtils.buildComment("01/01/2011 11:00", developer1), PluginTestUtils.buildComment("02/01/2011 10:30", developer1)))
                .put(2, Arrays.asList(PluginTestUtils.buildComment("01/01/2011 11:00", developer1)))
                .put(3, Arrays.asList(PluginTestUtils.buildComment("01/01/2011 14:30", developer2)))
            .build();
        
        issuesFetcher = PluginTestUtils.buildIssueFetcherMock(issueList, commentListIssueNumberMap);
    }
    
    private static IssuesFetcher issuesFetcher;
    private static String mockedUser = PluginTestUtils.getGitHubMockedUser();
    private static String mockedRepo = PluginTestUtils.getGitHubMockedRepo();

    private static IssuesCommentedBy instance;
    
    public void testAnalyzeIssuesNotCommentedBy(String  listMode, String status, Integer... expectedPostNumber) throws IOException {
        AnalysisRequest analysisRequest = new AnalysisRequest();
        analysisRequest.setName(instance.getName());
        analysisRequest.put(IssuesCommentedBy.KEY_IN_USER_LIST, "[Nonplay]");
        analysisRequest.put(IssuesCommentedBy.KEY_IN_USER_LIST_MODE, listMode);
        analysisRequest.put(IssueStatePluginHelper.KEY_IN_ISSUE_STATE, status);
        
        AnaylsisResult anaylsisResult = instance.analyzeIssues(analysisRequest, issuesFetcher, mockedUser, mockedRepo);
        System.out.println(anaylsisResult);
        
        Assert.assertEquals(instance.getName(), anaylsisResult.getName());        
        Assert.assertEquals(expectedPostNumber.length, Integer.parseInt(anaylsisResult.get(IssuesCommentedBy.KEY_OUT_TOTAL_COMMENTED_ISSUES)));
        Assert.assertEquals(Arrays.asList(expectedPostNumber), 
                ((List<Map>)PluginTestUtils.deserializeJsonFromMap(anaylsisResult, IssuesCommentedBy.KEY_OUT_COMMENTED_ISSUES, List.class))
                .stream().map((m)->((Double)m.get("number")).intValue()).collect(Collectors.toList()));
    }
    
    @Test
    public void testAnalyzeIssuesNotCommentedByExcludedAll() throws IOException {
        testAnalyzeIssuesNotCommentedBy(SelectionModePluginHelper.VALUE_IN_MODE_EXCLUDED, IssueStatePluginHelper.VALUE_IN_ISSUE_STATE_ANY, 3, 4);
    }
    
    @Test
    public void testAnalyzeIssuesNotCommentedByExcludedOpen() throws IOException {
        testAnalyzeIssuesNotCommentedBy(SelectionModePluginHelper.VALUE_IN_MODE_EXCLUDED, IssueStatePluginHelper.VALUE_IN_ISSUE_STATE_OPEN, 3);
    }
    
    @Test
    public void testAnalyzeIssuesNotCommentedByExcludedClosed() throws IOException {
        testAnalyzeIssuesNotCommentedBy(SelectionModePluginHelper.VALUE_IN_MODE_EXCLUDED, IssueStatePluginHelper.VALUE_IN_ISSUE_STATE_CLOSED, 4);
    }
    
    @Test
    public void testAnalyzeIssuesNotCommentedByIncludedAll() throws IOException {
        testAnalyzeIssuesNotCommentedBy(SelectionModePluginHelper.VALUE_IN_MODE_INCLUDED, IssueStatePluginHelper.VALUE_IN_ISSUE_STATE_ANY, 1, 2);
    }
    
    @Test
    public void testAnalyzeIssuesNotCommentedByIncludedOpen() throws IOException {
        testAnalyzeIssuesNotCommentedBy(SelectionModePluginHelper.VALUE_IN_MODE_INCLUDED, IssueStatePluginHelper.VALUE_IN_ISSUE_STATE_OPEN, 2);
    }
    
    @Test
    public void testAnalyzeIssuesNotCommentedByIncludedClosed() throws IOException {
        testAnalyzeIssuesNotCommentedBy(SelectionModePluginHelper.VALUE_IN_MODE_INCLUDED, IssueStatePluginHelper.VALUE_IN_ISSUE_STATE_CLOSED, 1);
    }
    
    
}
