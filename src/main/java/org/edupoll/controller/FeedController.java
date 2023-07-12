package org.edupoll.controller;

import org.edupoll.exception.NotExistUserException;
import org.edupoll.model.dto.request.CreateFeedRequest;
import org.edupoll.model.dto.response.FeedListResponse;
import org.edupoll.service.FeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
@CrossOrigin
public class FeedController {
	private final FeedService feedService;
	
	// 전체 글 목록 제공해주는 API
	@GetMapping
	public ResponseEntity<?> readAllFeedHandle(@RequestParam(defaultValue = "1") int page) {
		FeedListResponse list = feedService.feedAll(page);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	// 특정 글 제공해주는 API
	@GetMapping("/{feedId}")
	public ResponseEntity<?> readSpecificFeedHandle(@PathVariable Long feedId) throws NotExistUserException {
		return new ResponseEntity<>(feedService.feedDetail(feedId), HttpStatus.OK);
	}
	
	//신규 글 등록해주는 API
	@PostMapping("/private")
	public ResponseEntity<?> createNewFeedHandle(@AuthenticationPrincipal String principal, CreateFeedRequest req) {
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		feedService.create(principal, req);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	// 특정 글 삭제해주는 API
	@DeleteMapping("/private/{feedId}")
	public ResponseEntity<?> deleteSpecificFeedHandle(@AuthenticationPrincipal String principal, @PathVariable Long feedId) throws NotExistUserException {
		feedService.deleteFeed(principal, feedId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
