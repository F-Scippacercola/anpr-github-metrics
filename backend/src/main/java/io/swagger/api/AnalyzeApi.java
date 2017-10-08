/**
 * NOTE: This class is auto generated by the swagger code generator program (2.2.3).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.model.AnalysisRequest;
import io.swagger.model.AnaylsisResult;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-07T14:09:31.849+02:00")

@Api(value = "analyze", description = "the analyze API")
public interface AnalyzeApi {

    @ApiOperation(value = "", notes = "", response = AnaylsisResult.class, responseContainer = "List", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK - Operazione completata con successo", response = AnaylsisResult.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request - Richiesta non fornita con parametri validi", response = Void.class),
        @ApiResponse(code = 500, message = "Internal Error - Errore durante l'esecuzione dell'operazione", response = Void.class) })
    
    @RequestMapping(value = "/analyze/{gitHubUser}/{gitHubRepo}",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<List<AnaylsisResult>> analyzeGitHubUserGitHubRepoPost(@ApiParam(value = "",required=true ) @PathVariable("gitHubUser") String gitHubUser,@ApiParam(value = "",required=true ) @PathVariable("gitHubRepo") String gitHubRepo,@ApiParam(value = "The list of params for the plugins" ,required=true )  @Valid @RequestBody List<AnalysisRequest> request);

}
