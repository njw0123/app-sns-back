package org.edupoll.model.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
	private String previousPassword;
	private String newPassword;
}
