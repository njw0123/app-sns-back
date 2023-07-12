package org.edupoll.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidPasswordException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPasswordException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
