package org.edupoll.controller;

import java.io.IOException;

import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.model.dto.UserWrapper;
import org.edupoll.model.dto.request.ChangePasswordRequest;
import org.edupoll.model.dto.request.DeleteUserRequest;
import org.edupoll.model.dto.request.UpdateProfileRequest;
import org.edupoll.model.dto.response.LogonUserInfoResponse;
import org.edupoll.model.dto.response.UserResponseData;
import org.edupoll.service.JWTService;
import org.edupoll.service.KakaoAPIService;
import org.edupoll.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.NotSupportedException;
import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/private")
public class PrivateController {
	
	private final UserService userService;
	private final JWTService jwtService;
	private final KakaoAPIService kakaoAPIService;
	
	@GetMapping
	public ResponseEntity<?> getLogonUserHandle(Authentication authentication) {
		String principal = (String) authentication.getPrincipal();
		UserWrapper wrapper = userService.searchUserByEmail(principal);
		LogonUserInfoResponse response = new LogonUserInfoResponse(200, wrapper);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PatchMapping("/password")
	public ResponseEntity<UserResponseData> modifyPasswordHandle(@RequestHeader(name = "token", required = false) String token, ChangePasswordRequest dto) throws InvalidPasswordException {
		if (token == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		String email = jwtService.verifyToken(token);
		return new ResponseEntity<>(userService.validatePassword(email, dto), HttpStatus.OK);
	}
	
	@DeleteMapping
	public ResponseEntity<Void> deleteUserHandle(@AuthenticationPrincipal String principal, DeleteUserRequest req) throws InvalidPasswordException {
		if (req.getPassword() == null) {		// 소셜로 가입한 유저 삭제하기
			kakaoAPIService.sendUnlink(principal);
			userService.deleteSpecificSocialUser(principal);
		}else {		// 자체 관리중인 유저 삭제하기
			userService.deleteSpecificUser(principal, req.getPassword());
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	// 사용자 상태 (프로필 이미지 / 이름) 업데이트 처리할 API
	// 파일 업로드는 컨텐츠 타입이 multpart/form-data 로 들어옴.
	// (file과 text 유형이 섞여 있음)
	@PostMapping("/info")
	public ResponseEntity<LogonUserInfoResponse> updateProfileHandle(@AuthenticationPrincipal String principal, UpdateProfileRequest req) throws IOException, NotSupportedException {
		userService.modifySpecificUser(principal, req);
		var wrapper = userService.searchUserByEmail(principal);
		var response = new LogonUserInfoResponse(200, wrapper);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
