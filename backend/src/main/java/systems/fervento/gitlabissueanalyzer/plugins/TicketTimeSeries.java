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
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.egit.github.core.Issue;
import systems.fervento.gitlabissueanalyzer.plugins.utils.AnalysisResultBuilder;
import systems.fervento.gitlabissueanalyzer.IssuesAnalyzerPlugin;
import systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher;

/**
 *
 * @author nonplay
 */
public class TicketTimeSeries implements IssuesAnalyzerPlugin {

    @Override
    public String getName() {
        return "TicketTimeSeries";
    }
   
    private static final class IssueInfoPair {
        public int openedIssues = 0;
        public int closedIssues = 0;
        
        private void sumWith(IssueInfoPair other) {
            this.openedIssues += other.openedIssues;
            this.closedIssues += other.closedIssues;
        }
    };
    
    private IssueInfoPair retrieveIssueInfoPair(SortedMap<Date, IssueInfoPair> timeSeriesMap, Date date) {
        IssueInfoPair issueInfoPair = timeSeriesMap.get(date);
        if (issueInfoPair == null) {
            issueInfoPair = new IssueInfoPair();
            timeSeriesMap.put(date, issueInfoPair);
        }
        return issueInfoPair;
    }
    
    private IssueInfoPair retrieveIssueInfoPairFromTruncatedDate(SortedMap<Date, IssueInfoPair> timeSeriesMap, Date date) {
        return retrieveIssueInfoPair(timeSeriesMap, truncateTimeFromDate(date));
    }
    
    private Date truncateTimeFromDate(Date date) {
        return DateUtils.truncate(date, Calendar.DATE);
    }
    
    public final static String KEY_OUT_TIME_RANGE_DAYS = "timeRangeDays";
    public final static String KEY_OUT_PLOT = "plot";
    public final static String KEY_OUT_LABELS = "labels";
    public final static String KEY_OUT_SERIES = "series";
    
    @Override
    public AnaylsisResult analyzeIssues(AnalysisRequest analysisRequest, IssuesFetcher issuesFetcher, String gitHubUser, String gitHubRepo) {
        try {
            SortedMap<Date, IssueInfoPair> timeSeriesSortedMap = new TreeMap<>();            
            for (Issue issue : issuesFetcher.getIssues(gitHubUser, gitHubRepo)) {                
                IssueInfoPair openedDateIssueInfoPair = retrieveIssueInfoPairFromTruncatedDate(timeSeriesSortedMap, issue.getCreatedAt());
                openedDateIssueInfoPair.openedIssues++;
                
                if (issue.getClosedAt() != null) {
                    IssueInfoPair closedDateIssueInfoPair = retrieveIssueInfoPairFromTruncatedDate(timeSeriesSortedMap, issue.getClosedAt());
                    closedDateIssueInfoPair.closedIssues++;
                }
            }
            
            int timeRangeDays = timeSeriesSortedMap.size();
            List<String> labels = new ArrayList<>(timeRangeDays);
            List<Integer> openedSeries = new ArrayList<>(timeRangeDays);
            List<Integer> closedSeries = new ArrayList<>(timeRangeDays);

            IssueInfoPair accumulator = new IssueInfoPair();      
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy"); 
            for (Entry<Date, IssueInfoPair> entry : timeSeriesSortedMap.entrySet()) {
                labels.add(simpleDateFormat.format(entry.getKey()));
                accumulator.sumWith(entry.getValue());
                
                openedSeries.add(accumulator.openedIssues);
                closedSeries.add(accumulator.closedIssues);
            }
            
            return AnalysisResultBuilder.build(this)
                    .with(KEY_OUT_TIME_RANGE_DAYS, timeRangeDays)
                    .with(KEY_OUT_PLOT, 
                            ImmutableMap.of(KEY_OUT_LABELS, labels, 
                                            KEY_OUT_SERIES, Arrays.asList(openedSeries, closedSeries)))
                    .build();
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    
}