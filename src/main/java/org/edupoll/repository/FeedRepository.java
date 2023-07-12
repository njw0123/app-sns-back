package org.edupoll.repository;

import java.util.List;

import org.edupoll.model.entity.Feed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long>{
	List<Feed> findAllByOrderByIdDesc(Pageable page);
}
