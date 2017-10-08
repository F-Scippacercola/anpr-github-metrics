/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.plugins;

import io.swagger.model.AnalysisRequest;
import io.swagger.model.AnaylsisResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.egit.github.core.Issue;
import systems.fervento.gitlabissueanalyzer.plugins.utils.AnalysisResultBuilder;
import systems.fervento.gitlabissueanalyzer.IssuesAnalyzerPlugin;
import systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher;

/**
 *
 * @author nonplay
 */
public class IssuesClosedWithoutComments implements IssuesAnalyzerPlugin {
    
    public final static String KEY_OUT_TOTAL_NOT_COMMENTED_ISSUES = "totalNotCommentedIssues";
    public final static String KEY_OUT_NOT_COMMENTED_ISSUES = "notCommentedIssues";

    @Override
    public String getName() {
        return "IssuesClosedWithoutComments";
    }
    
    @Override
    public AnaylsisResult analyzeIssues(AnalysisRequest analysisRequest, IssuesFetcher issuesFetcher, String gitHubUser, String gitHubRepo) {
        try {
            List<Issue> issuedNotCommentedBy = new ArrayList<>();
            for (Issue issue : issuesFetcher.getIssues(gitHubUser, gitHubRepo)) {                
                if (issue.getComments() == 0) {
                    issuedNotCommentedBy.add(issue);
                }
            }
            
            return AnalysisResultBuilder.build(this)
                    .with(KEY_OUT_TOTAL_NOT_COMMENTED_ISSUES, issuedNotCommentedBy.size())
                    .with(KEY_OUT_NOT_COMMENTED_ISSUES, issuedNotCommentedBy)
                    .build();
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    
}