package org.edupoll.controller;

import org.edupoll.model.dto.KakaoAccessTokenWrapper;
import org.edupoll.model.dto.KakaoAccount;
import org.edupoll.model.dto.request.KakaoAuthorizedCallbackRequest;
import org.edupoll.model.dto.request.ValidateKakaoRequest;
import org.edupoll.model.dto.response.OAuthSignResponse;
import org.edupoll.model.dto.response.ValidateUserResponse;
import org.edupoll.service.JWTService;
import org.edupoll.service.KakaoAPIService;
import org.edupoll.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
@CrossOrigin
public class OAuthController {
	private final KakaoAPIService kakaoAPIService;
	private final JWTService jwtService;
	private final UserService userService;
	
	@Value("${kakao.restapi.key}")
	String kakaoRestApiKey;
	@Value("${kakao.redirect.url}")
	String kakaoRedirectUrl;
	
	// 카카오 인증 요청시 인증해야 될 주소 알려주는 API
	@GetMapping("/kakao")
	public ResponseEntity<OAuthSignResponse> oauthKakaoHandle() {
		// 카카오 인증을 할 수 있는 주소를 보내주면 됨
		var response = new OAuthSignResponse(200, "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+kakaoRestApiKey+"&redirect_uri="+kakaoRedirectUrl);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	// 카카오 인증 코드로 사용자 정보를 얻어내는 API
	@PostMapping("/kakao")
	public ResponseEntity<ValidateUserResponse> oauthKakaoPostHandle(ValidateKakaoRequest req) throws JsonMappingException, JsonProcessingException {
		KakaoAccessTokenWrapper wrapper = kakaoAPIService.getAccessToken(req.getCode());
		KakaoAccount account = kakaoAPIService.getUserInfo(wrapper.getAccessToken());
		userService.updateKakaoUser(account, wrapper.getAccessToken());
		log.info("kakao = {}", account.toString());
		String token = jwtService.createToken(account.getEmail());
		ValidateUserResponse response = new ValidateUserResponse(200, token, account.getEmail());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}
