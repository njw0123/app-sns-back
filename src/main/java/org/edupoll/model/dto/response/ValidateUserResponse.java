package org.edupoll.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateUserResponse {
	// 인증 성공시 보내주는 응답객체
	private int code;
	private String token;
	private String userEmail;
}
