package com.cha.markit.service;

import com.cha.markit.domain.User;
import com.cha.markit.dto.response.AuthResponse;
import com.cha.markit.exception.BusinessException;
import com.cha.markit.exception.ErrorCode;
import com.cha.markit.jwt.JwtTokenProvider;
import com.cha.markit.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${google.client-id}")
    private String googleClientId;

    public AuthResponse authenticateWithGoogle(String idToken) {
        GoogleIdToken.Payload payload = verifyIdToken(idToken);

        User user = userRepository.findByEmail(payload.getEmail())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .id(UUID.randomUUID().toString())
                                .email(payload.getEmail())
                                .name((String) payload.get("name"))
                                .provider("google")
                                .providerId(payload.getSubject())
                                .build()
                ));

        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail());

        return new AuthResponse(token, user.getName());
    }

    private GoogleIdToken.Payload verifyIdToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new BusinessException(ErrorCode.INVALID_ID_TOKEN);
            }

            return googleIdToken.getPayload();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ID_TOKEN_VERIFICATION_FAILED, e);
        }
    }
}