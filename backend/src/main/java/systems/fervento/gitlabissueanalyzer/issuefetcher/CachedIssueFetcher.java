/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.issuefetcher;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Striped;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;

/**
 * This class adds transparent caching to a {@link systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher} service.
 * 
 * @see systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher
 */
public class CachedIssueFetcher implements IssuesFetcher {
    
    // The delegatedIssueFetcher used when needed to retrieve the data
    private final IssuesFetcher delegatedIssueFetcher;
    
    /**
     * To enhance the parallelism during multiple requests but also to avoid
     * that concurrent requests to the same service could lead to bottlenecks,
     * we use a {@link Striped} object.
     * 
     * The memory/performance tradeoff (i.e. the number of STRIPES) is defined 
     * by the following constant.
     */
    private final int NUMBER_OF_STIPES = 512;
    private final Striped<Lock> stripedLocks = Striped.lock(NUMBER_OF_STIPES);
    
    /**
     * These are the max_size values for the cache on issues and comments
     * 
     * It still holds the LRU principle to delete objects when memory is out!
     */
    private final static int CACHE_ISSUES_CACHE_MAX_SIZE = 1000;
    private final static int CACHE_COMMENTS_CACHE_MAX_SIZE = 10000;
    
    /**
     * The maximum time until a value written in the cache is considered fresh.
     */
    private final static int CACHE_DURATION_TIME_MIN = 10;
    
    /**
     * The caches have been implemented thanks to the class {@link Cache}.
     */
    private final Cache<String, List<Issue>> issueCache = CacheBuilder.newBuilder()
         .maximumSize(CACHE_ISSUES_CACHE_MAX_SIZE)
         .expireAfterWrite(CACHE_DURATION_TIME_MIN, TimeUnit.MINUTES)
         .build();
    
    private final Cache<Integer, List<Comment>> commentsCache = CacheBuilder.newBuilder()
         .maximumSize(CACHE_COMMENTS_CACHE_MAX_SIZE)
         .expireAfterWrite(CACHE_DURATION_TIME_MIN, TimeUnit.MINUTES)
         .build();
    
    /**
     * The builder that requires the delegate to which to add transparent caching.
     * 
     * @param delegatedIssueFetcher the delegated service.
     */
    public CachedIssueFetcher(IssuesFetcher delegatedIssueFetcher) {
        this.delegatedIssueFetcher = delegatedIssueFetcher;
    }    
        
    /**
     * Computes an unique hash for the couple (user, repo).
     * 
     * @param user the repo\'s owner.
     * @param repo the repo.
     * @return a unique hash value for the couple (user, repo).
     */
    private String computeUniqueHash(String user, String repo) {
        char NOT_EXPECTED_CHAR_IN_USERNAME = '\t';
        return String.format("%s%c%s", user, NOT_EXPECTED_CHAR_IN_USERNAME, repo); 
    }

    /**
     * Honors the contract defined by {@link IssuesFetcher} but also adds caching.
     * 
     * @param user the repo\'s owner.
     * @param repo the repo.
     * @return the list of issues for the couple (user, repo).
     * @throws IOException in case of communication problems.
     * @see IssuesFetcher
     */
    @Override
    public List<Issue> getIssues(String user, String repo) throws IOException {
        String uniqueHash = computeUniqueHash(user, repo);
        List<Issue> issues = issueCache.getIfPresent(uniqueHash);
        if (issues == null) {
            Lock lock = stripedLocks.get(uniqueHash);
            lock.lock();
            try {
                issues = issueCache.getIfPresent(uniqueHash);
                if (issues == null) {
                    issues = delegatedIssueFetcher.getIssues(user, repo);
                    issueCache.put(uniqueHash, issues);
                }
            } finally {
                lock.unlock();
            }
        }
        return issues;
    }

    /**
     * Honors the contract defined by {@link IssuesFetcher} but also adds caching.
     * 
     * @param user the repo\'s owner.
     * @param repo the repo.
     * @param issueId the id of the issue associated to the comments to retrieve.
     * 
     * @return the list of comments for the couple (user, repo) related to issueId.
     * @throws IOException in case of communication problems.
     * @see IssuesFetcher
     */
    @Override
    public List<Comment> getIssueComments(String user, String repo, int issueId) throws IOException {
        List<Comment> comments = commentsCache.getIfPresent(issueId);
        if (comments == null) {
            Lock lock = stripedLocks.get(issueId);
            lock.lock();
            try {
                comments = commentsCache.getIfPresent(issueId);
                if (comments == null) {
                    comments = delegatedIssueFetcher.getIssueComments(user, repo, issueId);
                    commentsCache.put(issueId, comments);
                }
            } finally {
                lock.unlock();
            }
        }

        return comments;
    } 
            
}
