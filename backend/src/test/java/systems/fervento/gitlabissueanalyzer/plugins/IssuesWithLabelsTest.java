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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher;
import systems.fervento.gitlabissueanalyzer.plugins.utils.IssueStatePluginHelper;
import systems.fervento.gitlabissueanalyzer.plugins.utils.SelectionModePluginHelper;

/**
 *
 * @author nonplay
 */
public class IssuesWithLabelsTest {
    
    @BeforeClass
    public static void beforeAll() throws IOException {
        instance = new IssuesWithLabels();
        
        String label1 = "critical";
        String label2 = "discussion";
        List<Issue> issueList = 
            Arrays.asList(
                PluginTestUtils.buildIssue(1, "01/01/2011 10:00", "01/01/2011 11:00", Arrays.asList(label1)),
                PluginTestUtils.buildIssue(2, "01/01/2011 10:00", null, Arrays.asList(label1)),
                PluginTestUtils.buildIssue(3, "01/01/2011 10:00", null, Arrays.asList(label2)),
                PluginTestUtils.buildIssue(4, "01/01/2011 10:00", "01/01/2011 14:00", Collections.EMPTY_LIST)
            );
        Map<Integer, List<Comment>> commentListIssueNumberMap =
            (Map)ImmutableMap.builder()
                .put(1, Arrays.asList(PluginTestUtils.buildComment("01/01/2011 11:00", label1), PluginTestUtils.buildComment("02/01/2011 10:30", label1)))
                .put(2, Arrays.asList(PluginTestUtils.buildComment("01/01/2011 11:00", label1)))
                .put(3, Arrays.asList(PluginTestUtils.buildComment("01/01/2011 14:30", label2)))
            .build();
        
        issuesFetcher = PluginTestUtils.buildIssueFetcherMock(issueList, commentListIssueNumberMap);
    }
    
    private static IssuesFetcher issuesFetcher;
    private static String mockedUser = PluginTestUtils.getGitHubMockedUser();
    private static String mockedRepo = PluginTestUtils.getGitHubMockedRepo();

    private static IssuesWithLabels instance;
    
    public void testAnalyzeIssuesNotCommentedBy(String  listMode, String status, Integer... expectedPostNumber) throws IOException {
        AnalysisRequest analysisRequest = new AnalysisRequest();
        analysisRequest.setName(instance.getName());
        analysisRequest.put(IssuesWithLabels.KEY_IN_LABEL_LIST, "[critical]");
        analysisRequest.put(IssuesWithLabels.KEY_IN_LABEL_LIST_MODE, listMode);
        analysisRequest.put(IssueStatePluginHelper.KEY_IN_ISSUE_STATE, status);
        
        AnaylsisResult anaylsisResult = instance.analyzeIssues(analysisRequest, issuesFetcher, mockedUser, mockedRepo);
        System.out.println(anaylsisResult);
        
        Assert.assertEquals(instance.getName(), anaylsisResult.getName());        
        Assert.assertEquals(expectedPostNumber.length, Integer.parseInt(anaylsisResult.get(IssuesWithLabels.KEY_OUT_TOTAL_ISSUES)));
        Assert.assertEquals(Arrays.asList(expectedPostNumber), 
                ((List<Map>)PluginTestUtils.deserializeJsonFromMap(anaylsisResult, IssuesWithLabels.KEY_OUT_ISSUES, List.class))
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
