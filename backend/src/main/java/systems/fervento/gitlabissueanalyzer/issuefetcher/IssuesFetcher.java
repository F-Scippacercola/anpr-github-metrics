/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.issuefetcher;

import java.io.IOException;
import java.util.List;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;

/**
 * Define the contract for the plugins that fetches the issues from repos.
 * 
 * @author nonplay
 */
public interface IssuesFetcher {
    /**
     * Fetches the list of issues associated to a repo of a user.
     * 
     * @param user the repo\'s owner.
     * @param repo the repo.
     * @return the list of issues for the couple (user, repo).
     * @throws IOException in case of communication problems.
     */
    List<Issue> getIssues(String user, String repo) throws IOException;
    
    /**
     * Fetches the list of comments associated to an issue from the repo of a user.
     * 
     * @param user the repo\'s owner.
     * @param repo the repo.
     * @param issueId the id of the issue associated to the comments to retrieve.
     * 
     * @return the list of comments for the couple (user, repo) related to issueId.
     * @throws IOException in case of communication problems. 
     */
    List<Comment> getIssueComments(String user, String repo, int issueId) throws IOException;    
}
