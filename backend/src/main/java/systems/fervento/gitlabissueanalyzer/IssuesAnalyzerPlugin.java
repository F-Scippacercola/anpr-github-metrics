/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer;

import io.swagger.model.AnalysisRequest;
import io.swagger.model.AnaylsisResult;
import java.io.UncheckedIOException;
import systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher;

/**
 *
 * @author nonplay
 */
public interface IssuesAnalyzerPlugin {
    
    public AnaylsisResult analyzeIssues(AnalysisRequest analysisRequest, IssuesFetcher issuesFetcher, String gitHubUser, String gitHubRepo) throws UncheckedIOException;
    public String getName();
    
    public static class PluginNotAvailable extends RuntimeException {

        public PluginNotAvailable(String message) {
            super(message);
        }
        
    }
    
}
