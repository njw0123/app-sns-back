package org.edupoll.service;

import java.net.URI;

import org.edupoll.model.dto.KakaoAccessTokenWrapper;
import org.edupoll.model.dto.KakaoAccount;
import org.edupoll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoAPIService {
	private final UserRepository userRepository;
	@Value("${kakao.restapi.key}")
	String kakaoRestApiKey;
	@Value("${kakao.redirect.url}")
	String kakaoRedirectUrl;

	public KakaoAccessTokenWrapper getAccessToken(String code) {
		// 콜백으로 받은 인증코드를 이용해서 카카오에서 유저를 받아와야 한다.
		String tokenURL = "https://kauth.kakao.com/oauth/token";

		RestTemplate template = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		var body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", kakaoRestApiKey);
		body.add("redirect_uri", kakaoRedirectUrl);
		body.add("code", code);

		HttpEntity<MultiValueMap> entity = new HttpEntity<>(body, headers);

		ResponseEntity<KakaoAccessTokenWrapper> result = template.postForEntity(tokenURL, entity,
				KakaoAccessTokenWrapper.class);

		log.info("result.statusCode = {} : {} ", result.getStatusCode());
		log.info("result.body = {}", result.getBody());

		return result.getBody();
	}

	// accessToken 을 실제 카카오 사용자의 정보를 얻어와야 한다.
	public KakaoAccount getUserInfo(String accessToken) throws JsonMappingException, JsonProcessingException {
		String dest = "https://kapi.kakao.com/v2/user/me";

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		RestTemplate template = new RestTemplate();

		RequestEntity request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(dest));

		ResponseEntity<String> response = template.exchange(request, String.class);
		log.info("response.statusCode = {}", response.getStatusCode());
		log.info("response.body = {}", response.getBody());

		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(response.getBody());

		String email = node.get("id").asText("") + "@kakao.user";
		String nickname = node.get("kakao_account").get("profile").get("nickname").asText();
		String profileImage = node.get("kakao_account").get("profile").get("profile_image_url").asText();

		return new KakaoAccount(email, nickname, profileImage);
	}

	public void sendUnlink(String email) {
		String uri = "https://kapi.kakao.com/v1/user/unlink";
		String token = userRepository.findByEmail(email).getSocial();
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", "Bearer " + token);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		RestTemplate template = new RestTemplate();

		RequestEntity request = new RequestEntity<>(headers, HttpMethod.POST, URI.create(uri));

		ResponseEntity<String> response = template.exchange(request, String.class);
		log.info("unlink response = {}", response.getBody());
	}
}

/*
 * Spring framework 에서 REST API를 호출하는걸 도와주기 위해서 RestTemplate - 동기 WebClient -
 * 비동기
 */
