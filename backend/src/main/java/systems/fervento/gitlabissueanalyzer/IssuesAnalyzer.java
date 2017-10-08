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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import systems.fervento.gitlabissueanalyzer.IssuesAnalyzerPlugin.PluginNotAvailable;
import systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcherBuilder;
import systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher;
import systems.fervento.gitlabissueanalyzer.plugins.FirstReplyTime;
import systems.fervento.gitlabissueanalyzer.plugins.IssuesClosedWithoutComments;
import systems.fervento.gitlabissueanalyzer.plugins.IssuesCommentedBy;
import systems.fervento.gitlabissueanalyzer.plugins.IssuesWithLabels;
import systems.fervento.gitlabissueanalyzer.plugins.TicketClosingTime;
import systems.fervento.gitlabissueanalyzer.plugins.TicketGeneral;
import systems.fervento.gitlabissueanalyzer.plugins.TicketTimeSeries;

/**
 *
 * @author nonplay
 */
public class IssuesAnalyzer {

    private static final Logger LOG = Logger.getLogger(IssuesAnalyzer.class.getName());
    private static IssuesAnalyzer instance;
    
    public static IssuesAnalyzer getInstance() {
        // If first access
        if (instance == null) {
            // Acquire the lock
            synchronized (IssuesAnalyzer.class) {
                // If the object ha not been set by another concurrent thread 
                if (instance == null) {
                    instance = new IssuesAnalyzer(IssuesFetcherBuilder.buildDefault())
                            .registerPlugin(new FirstReplyTime())
                            .registerPlugin(new TicketClosingTime())
                            .registerPlugin(new TicketGeneral())
                            .registerPlugin(new TicketTimeSeries())
                            .registerPlugin(new IssuesCommentedBy())
                            .registerPlugin(new IssuesClosedWithoutComments())
                            .registerPlugin(new IssuesWithLabels());
                }
            }
        }
        return instance;
    }
    
    // To improve the testability for unit testing 
    protected static synchronized void setInstance(IssuesAnalyzer issuesAnalyzer) {
        if (instance != null) {
            throw new IllegalStateException("The singleton was already set!");
        }
        instance = issuesAnalyzer;
    }

    private final IssuesFetcher issuesFetcher;
    private final Map<String, IssuesAnalyzerPlugin> issuesAnalyzerPluginMap = new HashMap<>();

    public IssuesAnalyzer(IssuesFetcher issuesFetcher) {
        this.issuesFetcher = issuesFetcher;
    }

    public IssuesAnalyzer registerPlugin(IssuesAnalyzerPlugin plugin) {
        String pluginName = plugin.getName();
        issuesAnalyzerPluginMap.put(pluginName, plugin);
        LOG.log(Level.INFO, "Registered plugin: {0}", pluginName);
        return this;
    }
    
    public IssuesAnalyzer unregisterPlugin(String name) {
        issuesAnalyzerPluginMap.remove(name);
        LOG.log(Level.INFO, "Unregistered plugin: {0}", name);
        return this;
    }
            
    
    public List<AnaylsisResult> analyzeIssues(String gitHubUser, String gitHubRepo, List<AnalysisRequest> analysisRequestList) throws IOException, PluginNotAvailable {
        return analysisRequestList.stream()
                .parallel()
            .map(
                (analysisRequest) ->  {
                    String pluginName = analysisRequest.getName();
                    IssuesAnalyzerPlugin issuesAnalyzerPlugin = this.issuesAnalyzerPluginMap.get(pluginName);
                    if (issuesAnalyzerPlugin == null) {
                        throw new PluginNotAvailable("Cannot find the plugin: " + pluginName);
                    }
                    return issuesAnalyzerPlugin.analyzeIssues(analysisRequest, issuesFetcher, gitHubUser, gitHubRepo);
                }
            ).collect(Collectors.toList());
    }
    
}
