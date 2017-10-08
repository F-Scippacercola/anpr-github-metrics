/*
 *  IssueAnalyzer - https://github.com/F-Scippacercola/anpr-github-metrics
 *  Copyright (c) 2017 F. Scippacercola, E. Battista
 *
 *  See the website for additional information about the copyright.
 *  Please, visit also our website: http://fervento.systems
 */
package systems.fervento.gitlabissueanalyzer.plugins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.User;
import org.mockito.Matchers;
import static org.mockito.Matchers.any;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import systems.fervento.gitlabissueanalyzer.issuefetcher.IssuesFetcher;
import static org.mockito.Matchers.any;

/**
 *
 * @author nonplay
 */
public class PluginTestUtils {
    
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    
    public static <T> T deserializeJsonFromMap(Map<String, String> map, String key, Class<T> clasz) {
        if (map.containsKey(key) == false) {
            return null;
        }
        return deserializeJson(map.get(key), clasz);
    }
    
    public static <T> T deserializeJson(String json, Class<T> clasz) {
        return GSON.fromJson(json, clasz);
    }  
    
    public static String getGitHubMockedUser() {
        return "mockUser";
    }
    
    public static String getGitHubMockedRepo() {
        return "mockRemo";
    }
    
    public static IssuesFetcher buildIssueFetcherMock(List<Issue> issueList, Map<Integer, List<Comment>> commentListIssueNumberMap) throws IOException {
        IssuesFetcher issuesFetcher 
            =  Mockito.mock(IssuesFetcher.class);
        
        Mockito.doReturn(issueList).when(issuesFetcher).getIssues(any(), any());
        
        Mockito.when(issuesFetcher.getIssueComments(any(), any(), Matchers.anyInt())).thenAnswer(
            new Answer<List<Comment>>() {
                @Override
                public List<Comment> answer(InvocationOnMock invocation) throws Throwable {
                    int val = invocation.getArgumentAt(2, Integer.class);
                    return commentListIssueNumberMap.getOrDefault(val, Collections.EMPTY_LIST);
                }
            }
        );
        
        for (Issue issue : issueList) {
            List<Comment> issueCommentList = commentListIssueNumberMap.get(issue.getNumber());
            if (issueCommentList != null) {
                issue.setComments(issueCommentList.size());
            }
        }
        
        return issuesFetcher;
    }
    
    public static Comment buildComment(String createdAt, String userName) {
        try {
            Comment comment = new Comment();
            comment.setId(idGenerator++);
            comment.setCreatedAt(simpleDateFormat.parse(createdAt));
            
            if (userName != null) {
                User user = new User();
                user.setLogin(userName);
                comment.setUser(user);
            }
            
            return comment;
        } catch (ParseException e) {
            throw new RuntimeException(e);            
        }
    }
    
    
    
    public static Issue buildIssue(int number, String createdAt, String closedAt, List<String> labels) {
        try {
            Issue issue = new Issue();
            issue.setId(idGenerator++);
            issue.setNumber(number);
            issue.setCreatedAt(simpleDateFormat.parse(createdAt));
            if (closedAt != null) {
                issue.setClosedAt(simpleDateFormat.parse(closedAt));
                issue.setState(ISSUE_STATE_CLOSED);
            } else {
                issue.setState(ISSUE_STATE_OPEN);
            }
            
            if (labels != null) {
                List<Label> labelList = new ArrayList<Label>();
                for (String labelName : labels) {
                    Label label = new Label();
                    label.setName(labelName);
                    labelList.add(label);
                }
                issue.setLabels(labelList);
            }
            return issue;
        } catch (ParseException e) {
            throw new RuntimeException(e);            
        }
    }
    
    final private static String ISSUE_STATE_CLOSED = "closed";
    final private static String ISSUE_STATE_OPEN = "open";
    
    final private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm"); 
    public static int idGenerator = 0;
    
}
