package io.swagger.api;

import io.swagger.model.AnalysisRequest;

import io.swagger.annotations.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import systems.fervento.gitlabissueanalyzer.IssuesAnalyzer;
import systems.fervento.gitlabissueanalyzer.IssuesAnalyzerPlugin;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-07T14:09:31.849+02:00")
@Controller
@CrossOrigin
public class AnalyzeApiController implements AnalyzeApi {

    private static final Logger LOG = Logger.getLogger(AnalyzeApiController.class.getName());
    
    private IssuesAnalyzer issueAnalyzer = IssuesAnalyzer.getInstance();

    @Override
    public ResponseEntity analyzeGitHubUserGitHubRepoPost(@ApiParam(value = "",required=true ) @PathVariable("gitHubUser") String gitHubUser,
        @ApiParam(value = "",required=true ) @PathVariable("gitHubRepo") String gitHubRepo,
        @ApiParam(value = "The list of params for the plugins" ,required=true )  @Valid @RequestBody List<AnalysisRequest> request) {
        // do some magic!

        try {
            return new ResponseEntity<>(issueAnalyzer.analyzeIssues(gitHubUser, gitHubRepo, request), HttpStatus.OK);
            
        }  catch (IssuesAnalyzerPlugin.PluginNotAvailable e) {
            LOG.log(Level.WARNING, "Plugin not available!", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }  catch (Exception e) {
            LOG.log(Level.SEVERE, "Error in processing the request", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }             
        
    }

}
