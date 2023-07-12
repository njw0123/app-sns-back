package org.edupoll.model.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateFeedRequest {
	private String description;
	private List<MultipartFile> attaches;
}
