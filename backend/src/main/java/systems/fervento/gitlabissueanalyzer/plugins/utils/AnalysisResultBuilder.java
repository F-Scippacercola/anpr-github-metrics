/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.plugins.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.model.AnaylsisResult;
import systems.fervento.gitlabissueanalyzer.IssuesAnalyzerPlugin;

/**
 *
 * @author nonplay
 */
public class AnalysisResultBuilder {
    
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    
    final private AnaylsisResult anaylsisResult = new AnaylsisResult();
            
    public static AnalysisResultBuilder build(IssuesAnalyzerPlugin issuesAnalyzerPlugin) {
        AnalysisResultBuilder analysisResultBuilder = new AnalysisResultBuilder();
        analysisResultBuilder.anaylsisResult.setName(issuesAnalyzerPlugin.getName());
        return analysisResultBuilder;
    }
    
    
    public AnalysisResultBuilder with(String name, String value) {
        anaylsisResult.put(name, value);
        return this;
    }
    
    public AnalysisResultBuilder with(String name, Object value) {
        anaylsisResult.put(name, GSON.toJson(value));
        return this;
    }
    
    public AnaylsisResult build() {
        return anaylsisResult;
    }
}
