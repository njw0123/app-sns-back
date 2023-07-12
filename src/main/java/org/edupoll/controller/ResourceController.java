package org.edupoll.controller;

import java.net.MalformedURLException;

import org.edupoll.exception.NotExistUserException;
import org.edupoll.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {
	private final UserService userService;
	// 특정 경로로 왔을때 이미지를 보내주는
	// @GetMapping("/profile/{filename}")
	public ResponseEntity<Resource> getResourceHandle(HttpServletRequest request) throws NotExistUserException, MalformedURLException {
		request.getRequestURL().toString();
		Resource resource = userService.loadResource(request.getRequestURL().toString());
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_JPEG); // 파일 타입에 맞는 MIME 타입으로 설정

	    return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
}
