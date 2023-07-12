package org.edupoll.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyCodeRequest {
	@Email(message = "이메일 형식이 아닙니다.")
	String email;
	@NotBlank(message = "인증코드를 입력해주세요.")
	String code;
}
