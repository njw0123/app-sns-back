package org.edupoll.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class KakaoAccessTokenWrapper {
	@JsonProperty("token_type")
	private String tokenType;
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("expires_in")
	private int expiresIn;
}
