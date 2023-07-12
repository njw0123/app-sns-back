package org.edupoll.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.edupoll.exception.NotExistUserException;
import org.edupoll.model.dto.FeedAttachWrapper;
import org.edupoll.model.dto.FeedWrapper;
import org.edupoll.model.dto.request.CreateFeedRequest;
import org.edupoll.model.dto.response.FeedListResponse;
import org.edupoll.model.entity.Feed;
import org.edupoll.model.entity.FeedAttach;
import org.edupoll.repository.FeedAttachRepository;
import org.edupoll.repository.FeedRepository;
import org.edupoll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedService {
	private final FeedRepository feedRepository;
	private final FeedAttachRepository feedAttachRepository;
	private final UserRepository userRepository;

	@Value("${upload.basedir}")
	String baseDir;
	@Value("${upload.server}")
	String uploadServer;

	public FeedListResponse feedAll(int page) {
		FeedListResponse feedListResponse = new FeedListResponse();
		List<Feed> feedList = feedRepository.findAllByOrderByIdDesc(PageRequest.of(page, 10));
		List<FeedWrapper> feedWrapperList = feedList.stream().map(t -> new FeedWrapper(t)).toList();
		feedListResponse.setFeeds(feedWrapperList);
		feedListResponse.setTotal(feedList.size());
		return feedListResponse;
	}

	public FeedWrapper feedDetail(Long feedId) throws NotExistUserException {
		Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new NotExistUserException());
		feed.setViewCount(feed.getViewCount() + 1);
		feedRepository.save(feed);
		return new FeedWrapper(feed);
	}
	
	@Transactional
	public void create(String email, CreateFeedRequest req) {
		Feed feed = new Feed();
		if (req.getDescription() != null) {
			feed.setDescription(req.getDescription());
		}
		feed.setViewCount(0L);
		feed.setWriter(userRepository.findByEmail(email));
		Feed saved = feedRepository.save(feed);

		if (req.getAttaches() != null) {
			List<FeedAttach> multipartFiles = req.getAttaches().stream().map(t -> {
				FeedAttach feedAttach = new FeedAttach();
				feedAttach.setFeed(saved);
				feedAttach.setType(t.getContentType());

				File uploadDirectory = new File(baseDir + "/feed/" + saved.getId());
				uploadDirectory.mkdirs();

				String filename = System.currentTimeMillis()
						+ t.getOriginalFilename().substring(t.getOriginalFilename().lastIndexOf("."));

				File dest = new File(uploadDirectory, filename);

				try {
					t.transferTo(dest);
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
				}

				feedAttach.setMediaUrl(uploadServer + "/resource/feed/" + saved.getId() + "/" + filename);

				return feedAttachRepository.save(feedAttach);
			}).toList();
		}
	}
	
	@Transactional
	public void deleteFeed(String email, Long feedId) throws NotExistUserException {
		Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new NotExistUserException());
		if (!feed.getWriter().getEmail().equals(email)) {
			new NotExistUserException();
		}
		if (feed.getAttachs() != null) {
			feedAttachRepository.deleteByFeed(feed);
		}
		feedRepository.delete(feed);
	}
}
