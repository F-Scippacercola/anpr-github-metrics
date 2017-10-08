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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.egit.github.core.Issue;
import systems.fervento.gitlabissueanalyzer.plugins.utils.AnalysisResultBuilder;
import systems.fervento.gitlabissueanalyzer.IssuesAnalyzerPlugin;
import systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher;
import systems.fervento.gitlabissueanalyzer.plugins.utils.HistogramBuilder;

/**
 *
 * @author nonplay
 */
public abstract class SynteticStatisticalOperationPlugin implements IssuesAnalyzerPlugin {

    private static final Logger LOG = Logger.getLogger(SynteticStatisticalOperationPlugin.class.getName());
    
    public final static String KEY_IN_NUMBER_OF_BINS = "bins";
    public final static int VALUE_IN_NUMBER_OF_BINS_DEFAULT = 10;
    
    public final static String KEY_OUT_AVERAGE_TIME = "average";
    public final static String KEY_OUT_VARIANCE = "variance";
    public final static String KEY_OUT_HISTOGRAM = "histogram";
    
    protected int getNumberOfBins(AnalysisRequest analysisRequest) {
        try {
            if (analysisRequest.containsKey(KEY_IN_NUMBER_OF_BINS)) {
                return Integer.parseInt(analysisRequest.get(KEY_IN_NUMBER_OF_BINS));
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, "Bad value for the number of bins! Using the default one...", e);
        }
        return VALUE_IN_NUMBER_OF_BINS_DEFAULT;
    }
    
    protected abstract Optional<Double> processIssue(AnalysisRequest analysisRequest, IssuesFetcher issuesFetcher, String gitHubUser, String gitHubRepo, Issue issue) throws IOException;
    
    @Override
    public AnaylsisResult analyzeIssues(AnalysisRequest analysisRequest, IssuesFetcher issuesFetcher, String gitHubUser, String gitHubRepo) {
        try {
            HistogramBuilder histogramBuilder = new HistogramBuilder(getNumberOfBins(analysisRequest));
            for (Issue issue : issuesFetcher.getIssues(gitHubUser, gitHubRepo)) {
                Optional<Double> value = processIssue(analysisRequest, issuesFetcher, gitHubUser, gitHubRepo, issue);
                
                if (value.isPresent()) {
                    histogramBuilder.insertValue(value.get());
                }
            }
            histogramBuilder.compute();
            
            return AnalysisResultBuilder.build(this)
                    .with(KEY_OUT_AVERAGE_TIME, Double.toString(histogramBuilder.getAverage()))
                    .with(KEY_OUT_VARIANCE, Double.toString(histogramBuilder.getVariance()))
                    .with(KEY_OUT_HISTOGRAM, histogramBuilder.buildHistogram())
                    .build();
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    
}
