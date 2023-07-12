package org.edupoll.controller;

import java.util.Base64;

import org.edupoll.exception.AlreadyVerifiedException;
import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.exception.VerifyCodeException;
import org.edupoll.model.dto.request.ChangePasswordRequest;
import org.edupoll.model.dto.request.CreateUserRequest;
import org.edupoll.model.dto.request.ValidateUserRequest;
import org.edupoll.model.dto.request.VerifyCodeRequest;
import org.edupoll.model.dto.request.VerifyEmailRequest;
import org.edupoll.model.dto.response.UserResponseData;
import org.edupoll.model.dto.response.ValidateUserResponse;
import org.edupoll.model.dto.response.VerifyEmailResponse;
import org.edupoll.service.JWTService;
import org.edupoll.service.MailService;
import org.edupoll.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController {
	private final UserService userService;
	private final MailService mailService;
	private final JWTService jwtService;
	
	// 회원가입 처리
	@PostMapping("/join")
	public ResponseEntity<UserResponseData> joinHandle(@Valid CreateUserRequest dto) throws ExistUserEmailException, VerifyCodeException {
		return new ResponseEntity<>(userService.reqisterNewUser(dto), HttpStatus.CREATED);
	}
	
	// 이메일 사용가능한지 아닌지 확인해주는 API
	@GetMapping("/available")
	public ResponseEntity<Void> availableHandle(@Valid VerifyEmailRequest req) throws ExistUserEmailException {
		userService.emailAvailableCheck(req);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	// 로그인 처리
	@PostMapping("/validate")
	public ResponseEntity<ValidateUserResponse> validateHandle(@Valid ValidateUserRequest req) throws NotExistUserException, InvalidPasswordException {
		userService.validateUser(req);
		
		String token = jwtService.createToken(req.getEmail());
		var response = new ValidateUserResponse(200, token, req.getEmail());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// 이메일 인증코드 발급해주는 API
	@PostMapping("/verify-email")
	public ResponseEntity<VerifyEmailResponse> mailCodeHandle(@Valid VerifyEmailRequest req) throws MessagingException, AlreadyVerifiedException {
		mailService.sendVerifactionCode(req);
		return new ResponseEntity<>(new VerifyEmailResponse(200, "이메일 인증코드가 정상 발급 되었습니다."), HttpStatus.OK);
	}
	
	// 이메일 인증코드 검증해주는 API
	@PatchMapping("/verify-email")
	public ResponseEntity<VerifyEmailResponse> verifyCodeHandle(@Valid VerifyCodeRequest req) throws VerifyCodeException {
		userService.verifySpecificCode(req);
		return new ResponseEntity<>(new VerifyEmailResponse(200, "인증 완료 되었습니다."), HttpStatus.OK);
	}
	
}
