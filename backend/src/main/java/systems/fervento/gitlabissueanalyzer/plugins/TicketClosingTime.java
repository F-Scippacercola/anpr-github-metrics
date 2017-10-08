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
import java.util.Date;
import java.util.Optional;
import org.eclipse.egit.github.core.Issue;
import systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher;
import systems.fervento.gitlabissueanalyzer.plugins.utils.TimeUtils;

/**
 *
 * @author nonplay
 */
public class TicketClosingTime extends SynteticStatisticalOperationPlugin {

    @Override
    public String getName() {
        return "TicketClosingTime";
    }
    
    @Override
    protected Optional<Double> processIssue(AnalysisRequest analysisRequest, IssuesFetcher issuesFetcher, String gitHubUser, String gitHubRepo, Issue issue) throws IOException {
        long openTime = issue.getCreatedAt().getTime();
        Date closedAt = issue.getClosedAt();
        
        if (closedAt != null) {
            return Optional.of(TimeUtils.convertMsToHours((closedAt.getTime() - openTime)));
        }
        return Optional.empty();
    }
    
}
