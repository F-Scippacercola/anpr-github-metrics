/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.plugins.utils;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author nonplay
 */
public class TimeUtils {
    public static double convertMsToHours(long time_msec) {
        return time_msec/ (double)(TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS));
    }
    
}
