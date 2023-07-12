package org.edupoll.repository;

import org.edupoll.model.entity.Feed;
import org.edupoll.model.entity.FeedAttach;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedAttachRepository extends JpaRepository<FeedAttach, Long> {
	void deleteByFeed(Feed feed);
}
