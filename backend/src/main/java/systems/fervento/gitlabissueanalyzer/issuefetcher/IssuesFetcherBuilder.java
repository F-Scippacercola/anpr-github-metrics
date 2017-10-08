/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.issuefetcher;

/**
 *
 * @author nonplay
 */
public class IssuesFetcherBuilder {
    
    public static IssuesFetcher buildDefault() {
        return new CachedIssueFetcher(new RESTGitHubIssueFetcher());
    }
    
}
