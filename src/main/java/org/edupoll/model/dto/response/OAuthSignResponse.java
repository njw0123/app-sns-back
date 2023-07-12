package org.edupoll.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OAuthSignResponse {
	private int code;
	private String authUri;
}
