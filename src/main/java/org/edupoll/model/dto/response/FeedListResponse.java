package org.edupoll.model.dto.response;

import java.util.List;

import org.edupoll.model.dto.FeedWrapper;

import lombok.Data;

@Data
public class FeedListResponse {
	private int total;
	private List<FeedWrapper> feeds;
}
