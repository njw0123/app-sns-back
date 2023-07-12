package org.edupoll.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JWTService {
	@Value("${jwt.secret.key}")
	String secretKey;
	
	public String createToken(String email) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		return JWT.create()
				.withIssuer("finalApp")
				.withIssuedAt(new Date(System.currentTimeMillis()))
				.withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
				.withClaim("email", email)
				.sign(algorithm);
		
	}
	
	public String verifyToken(String token) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		var verifier =  JWT.require(algorithm).build();
		DecodedJWT decodedJWT = verifier.verify(token);

		return decodedJWT.getClaim("email").asString();
	}

}
