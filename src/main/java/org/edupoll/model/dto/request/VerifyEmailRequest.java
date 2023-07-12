package org.edupoll.model.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class VerifyEmailRequest {
	@Email(message = "이메일 형식이 아닙니다.")
	private String email;
}
