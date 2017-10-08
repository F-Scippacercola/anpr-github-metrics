/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.issuefetcher;

import com.google.common.base.Strings;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;

/**
 *
 * @author nonplay
 */
public class RESTGitHubIssueFetcher implements IssuesFetcher {
    private final GitHubClient client = new GitHubClient();
    private final IssueService issueService = new IssueService(client);

    private final static String ENV_GITHUB_OAUTH_TOKEN = "GITLAB_OAUTH_TOKEN";
    
    public RESTGitHubIssueFetcher() {
        String githubOAuthTocken = System.getenv(ENV_GITHUB_OAUTH_TOKEN);
        if (!Strings.isNullOrEmpty(githubOAuthTocken)) {
            client.setOAuth2Token(githubOAuthTocken);   
        }
    }
    
        
    @Override
    public List<Issue> getIssues(String gitHubUser, String gitHubRepo) throws IOException {
        return issueService.getIssues(gitHubUser, gitHubRepo, Collections.singletonMap("state", "all"));
    }

    @Override
    public List<Comment> getIssueComments(String gitHubUser, String gitHubRepo, int issueId) throws IOException {
        return issueService.getComments(gitHubUser, gitHubRepo, issueId);
    }
    
}
