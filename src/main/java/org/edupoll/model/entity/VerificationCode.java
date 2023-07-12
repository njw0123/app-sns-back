package org.edupoll.model.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "verificationCodes")
public class VerificationCode {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String code; // 인증코드
	private String email; // 인증코드를 발급시킨 이메일
	private Date created; // 발급된 날짜
	private String state; // 통과여부
	
}
