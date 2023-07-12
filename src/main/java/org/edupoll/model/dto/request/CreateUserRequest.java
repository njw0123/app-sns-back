package org.edupoll.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {
	@Email
	private String email;
	@NotBlank
	private String password;
	@NotBlank
	private String name;
}
