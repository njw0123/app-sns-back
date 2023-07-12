package org.edupoll.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidateUserRequest {
	@NotBlank(message = "아이디를 입력해주세요.")
	@Email(message = "이메일 형식이 아닙니다.")
	private String email;
	@NotBlank(message = "비밀번호를 입력하세요.")
	private String password;
}
