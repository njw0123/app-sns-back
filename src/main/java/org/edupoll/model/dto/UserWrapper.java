package org.edupoll.model.dto;

import org.edupoll.model.entity.User;

import lombok.Data;

@Data
public class UserWrapper {
	private Long id;
	private String name;
	private String email;
	private String profileImage;
	
	public UserWrapper(User entity) {
		this.id = entity.getId();
		this.name = entity.getName();
		this.email = entity.getEmail();
		this.profileImage = entity.getProfileImage();
	}
}
