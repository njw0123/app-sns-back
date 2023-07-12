package org.edupoll.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotExistUserException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotExistUserException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
