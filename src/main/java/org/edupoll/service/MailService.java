package org.edupoll.service;

import java.util.Date;
import java.util.Optional;

import org.edupoll.exception.AlreadyVerifiedException;
import org.edupoll.model.dto.request.VerifyEmailRequest;
import org.edupoll.model.entity.VerificationCode;
import org.edupoll.repository.VerificationCodeRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

	private final JavaMailSender javaMailSender;
	private final VerificationCodeRepository verificationCodeRepository;

	@Transactional
	public void sendVerifactionCode(VerifyEmailRequest req) throws AlreadyVerifiedException {
		// 이미 인증을 통과했는지 확인
		Optional<VerificationCode> found = verificationCodeRepository.findByEmail(req.getEmail());
		if (found.isPresent() && found.get().getState() == "Y") {
			throw new AlreadyVerifiedException();
		}
		
		int secretNum = (int) (Math.random() * 1000000);
		String code = String.format("%06d", secretNum);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(req.getEmail());
		message.setSubject("[파이날앱] 인증코드를 보내 드립니다.");
		message.setText("""
					이메일 본인 인증 절차에 따라 인증코드를 보내드립니다.

					인증코드 : %s
				""".formatted(code));
		javaMailSender.send(message);

		if (verificationCodeRepository.existsByEmail(req.getEmail())) {
			VerificationCode verificationCode = found.get();
			verificationCode.setCode(code);
			verificationCode.setCreated(new Date());
			verificationCodeRepository.save(verificationCode);
		} else {
			VerificationCode verificationCode = new VerificationCode();
			verificationCode.setCode(code);
			verificationCode.setEmail(req.getEmail());
			verificationCode.setCreated(new Date());
			verificationCode.setState("N");
			verificationCodeRepository.save(verificationCode);
		}
	}

	/*
	 * public void sendVerifactionCode(VerifyEmailRequest dto) throws
	 * MessagingException {
	 * 
	 * Random random = new Random(); int randNum =random.nextInt(1_000_000); String
	 * code = String.format("%06d", randNum);
	 * 
	 * MimeMessage message = javaMailSender.createMimeMessage(); MimeMessageHelper
	 * helper = new MimeMessageHelper(message); helper.setTo(dto.getEmail());
	 * helper.setSubject("메일 인증번호");
	 * 
	 * helper.setText(""" <div> <h1>메일 인증번호</h1> <p> 인증번호 : <i>%s</i> </p> </div>
	 * """.formatted(code), true); javaMailSender.send(message);
	 * 
	 * if (verificationCodeRepository.existsByEmail(dto.getEmail())) {
	 * VerificationCode verificationCode =
	 * verificationCodeRepository.findByEmail(dto.getEmail());
	 * verificationCode.setCode(code); verificationCode.setCreated(new Date());
	 * verificationCodeRepository.save(verificationCode); }else { VerificationCode
	 * verificationCode = new VerificationCode(); verificationCode.setCode(code);
	 * verificationCode.setEmail(dto.getEmail()); verificationCode.setCreated(new
	 * Date()); verificationCode.setState("N");
	 * verificationCodeRepository.save(verificationCode); } }
	 */
}
