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
import org.eclipse.egit.github.core.Issue;
import systems.fervento.gitlabissueanalyzer.plugins.utils.AnalysisResultBuilder;
import systems.fervento.gitlabissueanalyzer.IssuesAnalyzerPlugin;
import systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher;

/**
 *
 * @author nonplay
 */
public class TicketGeneral implements IssuesAnalyzerPlugin {
    
    public final static String KEY_OUT_OPEN_TICKETS = "openTickets";
    public final static String KEY_OUT_CLOSED_TICKETS = "closedTickets";

    @Override
    public String getName() {
        return "TicketGeneral";
    }
   
    @Override
    public AnaylsisResult analyzeIssues(AnalysisRequest analysisRequest, IssuesFetcher issuesFetcher, String gitHubUser, String gitHubRepo) {
        try {
            long openTickets = 0;
            long closedTickets = 0;
            
            for (Issue issue : issuesFetcher.getIssues(gitHubUser, gitHubRepo)) {                
                if (issue.getClosedAt() == null) {
                    openTickets++;
                } else {
                    closedTickets++;                    
                }
            }
            
            return AnalysisResultBuilder.build(this)
                    .with(KEY_OUT_OPEN_TICKETS, openTickets)
                    .with(KEY_OUT_CLOSED_TICKETS, closedTickets)
                    .build();
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    
}