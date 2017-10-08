/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.plugins.utils;

import io.swagger.model.AnalysisRequest;
import org.eclipse.egit.github.core.Issue;

/**
 *
 * @author nonplay
 */
public class IssueStatePluginHelper {

    public final static String KEY_IN_ISSUE_STATE = "issueState";
    public final static String VALUE_IN_ISSUE_STATE_OPEN = "open";
    public final static String VALUE_IN_ISSUE_STATE_CLOSED = "closed";
    public final static String VALUE_IN_ISSUE_STATE_ANY = "any";
    
     public static String deserializeIssueState(AnalysisRequest analysisRequest) {
        String issueState = analysisRequest.getOrDefault(KEY_IN_ISSUE_STATE, VALUE_IN_ISSUE_STATE_OPEN);
        if (VALUE_IN_ISSUE_STATE_OPEN.equalsIgnoreCase(issueState)) { 
            return VALUE_IN_ISSUE_STATE_OPEN;
        } else if (VALUE_IN_ISSUE_STATE_CLOSED.equalsIgnoreCase(issueState)) { 
            return VALUE_IN_ISSUE_STATE_CLOSED;
        } else if (VALUE_IN_ISSUE_STATE_ANY.equalsIgnoreCase(issueState)) { 
            return null;
        }
        throw new IllegalArgumentException("Invalid value for " + KEY_IN_ISSUE_STATE + ": " + issueState);
    }
     
     public static boolean satisfyState(Issue issue, String desiredState) {
         return (desiredState == null || desiredState.equalsIgnoreCase(issue.getState()));
     }
    
}
