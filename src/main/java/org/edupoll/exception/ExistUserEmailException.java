package org.edupoll.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ExistUserEmailException extends Exception {

	private static final long serialVersionUID = 1L;

	public ExistUserEmailException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
}
