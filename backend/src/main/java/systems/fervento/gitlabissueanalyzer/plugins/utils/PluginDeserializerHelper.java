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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author nonplay
 */
public class PluginDeserializerHelper {
    public final static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static <T> List<T> deserializeList(Map<String, String> stringMap, String key) {
        String jsonList = stringMap.get(key);
        if (jsonList != null) { 
            return GSON.fromJson(jsonList, List.class);
        }
        return Collections.EMPTY_LIST;
    }    
}
