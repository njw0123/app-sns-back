package org.edupoll.model.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity(name = "feedAttachs")
@Data
public class FeedAttach {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "feedId")
	private Feed feed;
	private String type;
	private String mediaUrl;
}
