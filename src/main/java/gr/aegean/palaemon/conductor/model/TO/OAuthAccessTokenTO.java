package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthAccessTokenTO {

    @JsonProperty("access_token")
    private String accessToken;
}
