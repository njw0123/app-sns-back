package org.edupoll.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
	private int code;
	private String cause;
	private long timeStamp = System.currentTimeMillis();
	
	public ErrorResponse(int code, String cause) {
		super();
		this.code = code;
		this.cause = cause;
	}	
	
}
