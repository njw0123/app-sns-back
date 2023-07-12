package org.edupoll.repository;

import org.edupoll.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
	User findByEmail(String email);
}
