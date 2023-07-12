package org.edupoll.model.dto;

import java.util.List;

import org.edupoll.model.entity.Feed;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedWrapper {
	private Long id;
	private UserWrapper writer;
	private String description;
	private Long viewCount;
	private List<FeedAttachWrapper> attaches;
	
	public FeedWrapper(Feed entity) {
		this.id = entity.getId();
		this.writer = new UserWrapper(entity.getWriter());
		this.description = entity.getDescription();
		this.viewCount = entity.getViewCount();
		this.attaches = entity.getAttachs().stream().map(t -> new FeedAttachWrapper(t)).toList();
	}
}
