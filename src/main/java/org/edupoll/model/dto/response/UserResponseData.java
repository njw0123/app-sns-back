package org.edupoll.model.dto.response;

import org.edupoll.model.entity.User;

import lombok.Data;

@Data
public class UserResponseData {
	private Long id;
	private String email;
	private String password;
	private String name;
	private String profileImage;
	private String social;
	
	public UserResponseData(User entity) {
		this.id = entity.getId();
		this.email = entity.getEmail();
		this.password = entity.getPassword();
		this.name = entity.getName();
		this.profileImage = entity.getProfileImage();
		this.social = entity.getSocial();
	}
}
