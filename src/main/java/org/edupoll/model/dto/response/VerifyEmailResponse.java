package org.edupoll.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyEmailResponse {
	private int code;
	private String message;
}
