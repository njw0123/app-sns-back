package org.edupoll.config;

import org.edupoll.exception.AlreadyVerifiedException;
import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.exception.VerifyCodeException;
import org.edupoll.model.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;

@ControllerAdvice
public class ExceptionHandlerConfiguration {
	@ExceptionHandler(ExistUserEmailException.class)
	public ResponseEntity<ErrorResponse> exisetUserEmailExceptionHandle(ExistUserEmailException ex) {
		ErrorResponse response = new ErrorResponse(400, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException ex) {
		var response = new ErrorResponse(400, ex.getBindingResult().getFieldError().getDefaultMessage());
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NotExistUserException.class)
	public ResponseEntity<Void> notExistUserExceptionHandle(NotExistUserException ex) {
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<ErrorResponse> invalidPasswordExceptionHandle(InvalidPasswordException ex) {
		var response = new ErrorResponse(401, "비밀번호가 틀렸습니다.");
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(VerifyCodeException.class)
	public ResponseEntity<ErrorResponse> verificationCodeMismatchExceptionHandle(VerifyCodeException ex) {
		ErrorResponse response = new ErrorResponse(400, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(AlreadyVerifiedException.class)
	public ResponseEntity<Void> alreadyVerifiedExceptionHandle(AlreadyVerifiedException ex) {
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({JWTDecodeException.class, TokenExpiredException.class})
	public ResponseEntity<ErrorResponse> jwtExceptionHandle(Exception ex) {
		var response = new ErrorResponse(401, "JWT Token is expired or damaged ");
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}
}
