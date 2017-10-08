/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.plugins.utils;

import io.swagger.model.AnalysisRequest;

/**
 *
 * @author nonplay
 */
public class SelectionModePluginHelper {
    public final static String VALUE_IN_MODE_INCLUDED = "included";
    public final static String VALUE_IN_MODE_EXCLUDED = "excluded";    
    
    public static boolean deserializeSelectionMode(AnalysisRequest analysisRequest, String key, String defaultValue) {
        String selectionMode = analysisRequest.getOrDefault(key, defaultValue);
        if (VALUE_IN_MODE_EXCLUDED.equalsIgnoreCase(selectionMode)) { 
            return false;
        } else if (VALUE_IN_MODE_INCLUDED.equalsIgnoreCase(selectionMode)) { 
            return true;
        }
        throw new IllegalArgumentException("Invalid value for " + key + ": " + selectionMode);
    }
    
}
