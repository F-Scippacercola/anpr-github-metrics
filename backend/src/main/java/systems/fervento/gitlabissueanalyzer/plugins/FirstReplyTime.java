/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.plugins;

import io.swagger.model.AnalysisRequest;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;
import org.eclipse.egit.github.core.Issue;
import systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher;
import systems.fervento.gitlabissueanalyzer.plugins.utils.TimeUtils;

/**
 *
 * @author nonplay
 */
public class FirstReplyTime extends SynteticStatisticalOperationPlugin {

    @Override
    public String getName() {
        return "FirstReplyTime";
    }
    
    @Override
    protected Optional<Double> processIssue(AnalysisRequest analysisRequest, IssuesFetcher issuesFetcher, String gitHubUser, String gitHubRepo, Issue issue) throws IOException {
        OptionalLong optionalLong 
                = issuesFetcher.getIssueComments(gitHubUser, gitHubRepo, issue.getNumber()).stream()
                        .mapToLong((c) -> c.getCreatedAt().getTime()).min();
        
        if (optionalLong.isPresent()) {
            long minTimeToReply_msec = optionalLong.getAsLong() - issue.getCreatedAt().getTime();
            return Optional.of(TimeUtils.convertMsToHours(minTimeToReply_msec));
        }
        return Optional.empty();
    }
    
}
