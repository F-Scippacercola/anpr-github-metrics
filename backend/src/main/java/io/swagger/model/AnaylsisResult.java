package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * a (key, value) map.
 */
@ApiModel(description = "a (key, value) map.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-07T14:09:31.849+02:00")

public class AnaylsisResult extends HashMap<String, String>  {

  public AnaylsisResult name(String name) {
    this.setName(name);
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(value = "")

  @JsonProperty("name")
  public String getName() {
    return get("name");
  }

  public void setName(String name) {
    this.put("name", name);
  }
  
}

