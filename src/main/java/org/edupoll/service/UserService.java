package org.edupoll.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.Optional;

import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.InvalidPasswordException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.exception.VerifyCodeException;
import org.edupoll.model.dto.KakaoAccount;
import org.edupoll.model.dto.UserWrapper;
import org.edupoll.model.dto.request.ChangePasswordRequest;
import org.edupoll.model.dto.request.CreateUserRequest;
import org.edupoll.model.dto.request.UpdateProfileRequest;
import org.edupoll.model.dto.request.ValidateUserRequest;
import org.edupoll.model.dto.request.VerifyCodeRequest;
import org.edupoll.model.dto.request.VerifyEmailRequest;
import org.edupoll.model.dto.response.UserResponseData;
import org.edupoll.model.entity.ProfileImage;
import org.edupoll.model.entity.User;
import org.edupoll.model.entity.VerificationCode;
import org.edupoll.repository.ProfileImageRepository;
import org.edupoll.repository.UserRepository;
import org.edupoll.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.NotSupportedException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	private final UserRepository userRepository;
	private final VerificationCodeRepository verificationCodeRepository;
	private final ProfileImageRepository profileImageRepository;

	@Value("${upload.basedir}")
	String baseDir;
	@Value("${upload.server}")
	String uploadServer;

	@Transactional
	public UserResponseData reqisterNewUser(CreateUserRequest dto) throws ExistUserEmailException, VerifyCodeException {
		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new ExistUserEmailException("이미 가입된 계정입니다.");
		}
		if (!verificationCodeRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new VerifyCodeException("인증코드 검증 기록이 존재하지 않습니다.")).getState().equals("Y")) {
			throw new VerifyCodeException("아직 인증이 되지 않은 계정 입니다.");
		}
		User one = new User();
		one.setEmail(dto.getEmail());
		one.setName(dto.getName());
		one.setPassword(dto.getPassword());
		userRepository.save(one);
		return new UserResponseData(one);
	}

	@Transactional
	public UserResponseData validateUser(ValidateUserRequest dto)
			throws NotExistUserException, InvalidPasswordException {
		if (!userRepository.existsByEmail(dto.getEmail())) {
			throw new NotExistUserException();
		} else if (!userRepository.findByEmail(dto.getEmail()).getPassword().equals(dto.getPassword())) {
			throw new InvalidPasswordException();
		}
		return new UserResponseData(userRepository.findByEmail(dto.getEmail()));
	}

	public void verifySpecificCode(@Valid VerifyCodeRequest req) throws VerifyCodeException {
		Optional<VerificationCode> result = verificationCodeRepository.findByEmail(req.getEmail());

		VerificationCode found = result.orElseThrow(() -> new VerifyCodeException("인증코드 발급 받은 적이 없다."));
		long elapsed = System.currentTimeMillis() - found.getCreated().getTime();
		if (elapsed > 1000 * 60 * 10) {
			throw new VerifyCodeException("인증코드 유효시간이 만료되었습니다.");
		}
		if (!found.getCode().equals(req.getCode())) {
			throw new VerifyCodeException("인증코드가 일치하지 않습니다.");
		}

		found.setState("Y");
		verificationCodeRepository.save(found);
	}

	public UserResponseData validatePassword(String email, ChangePasswordRequest dto) throws InvalidPasswordException {
		if (!userRepository.findByEmail(email).getPassword().equals(dto.getPreviousPassword())) {
			throw new InvalidPasswordException();
		}
		User user = userRepository.findByEmail(email);
		user.setPassword(dto.getNewPassword());
		userRepository.save(user);
		return new UserResponseData(user);
	}

	@Transactional
	public void deleteSpecificUser(String email, String password) throws InvalidPasswordException {
		User user = userRepository.findByEmail(email);
		if (!user.getPassword().equals(password)) {
			throw new InvalidPasswordException();
		}
		if (verificationCodeRepository.existsByEmail(email)) {
			verificationCodeRepository.delete(verificationCodeRepository.findByEmail(email).get());
		}
		userRepository.delete(user);
	}

	@Transactional
	public void emailAvailableCheck(@Valid VerifyEmailRequest req) throws ExistUserEmailException {
		boolean rst = userRepository.existsByEmail(req.getEmail());
		if (rst) {
			throw new ExistUserEmailException();
		}
	}

	public void updateKakaoUser(KakaoAccount account, String accessToken) {
		User _user = userRepository.findByEmail(account.getEmail());
		if (_user != null) {
			User saved = _user;
			saved.setSocial(accessToken);
			userRepository.save(saved);
		} else {
			User user = new User();
			user.setEmail(account.getEmail());
			user.setName(account.getNickname());
			user.setProfileImage(account.getProfileImage());
			user.setSocial(accessToken);
			userRepository.save(user);
		}
	}

	public void deleteSpecificSocialUser(String email) {
		userRepository.delete(userRepository.findByEmail(email));
	}

	@Transactional
	// 특정유저 정보 업데이트
	public void modifySpecificUser(String userEmail, UpdateProfileRequest request)
			throws IOException, NotSupportedException {
//		log.info("req.name = {}", request.getName());
//		log.info("req.profile = {} / {}", request.getProfile().getContentType(), request.getProfile().getOriginalFilename());
		
		var foundUser = userRepository.findByEmail(userEmail); // 있는지 없는지 체크
		foundUser.setName(request.getName());
		
		if (request.getProfile() != null) {
			// 리퀘스트 객체에서 파일 정보를 뽑자
			MultipartFile multi = request.getProfile();
			// 해당 파일이 컨텐츠 타입이 이미지인 경우에만 처리
			if (!multi.getContentType().startsWith("image/")) {
				throw new NotSupportedException("이미지 파일만 설정가능합니다.");
			}

			// 파일을 옮기는 작업
			// 기본 세이브경로는 propertis에서
			String emailEncoded = new String(Base64.getEncoder().encode(userEmail.getBytes()));

			File saveDir = new File(baseDir + "/profile/" + emailEncoded);
			saveDir.mkdirs();

			// 파일명은 로그인사용자의 이메일주소를 활용해서
			String filename = System.currentTimeMillis()
					+ multi.getOriginalFilename().substring(multi.getOriginalFilename().lastIndexOf("."));

			File dest = new File(saveDir, filename);

			// 두개 조합해서 옮길 장소 설정
			// 옮겨두기
			multi.transferTo(dest); // 업로드 됨.
			foundUser.setProfileImage(uploadServer + "/resource/profile/" + emailEncoded + "/" + filename);
		}

		// 파일 정보를 DB에 insert
		userRepository.save(foundUser);
	}

	public Resource loadResource(String url) throws NotExistUserException, MalformedURLException {
		ProfileImage found = profileImageRepository.findTop1ByUrl(url).orElseThrow(() -> new NotExistUserException());
		return new FileUrlResource(found.getFileAddress());
	}

	public UserWrapper searchUserByEmail(String tokenEmailValue) {
		return new UserWrapper(userRepository.findByEmail(tokenEmailValue));
	}
}
