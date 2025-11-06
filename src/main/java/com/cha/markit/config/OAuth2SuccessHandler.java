package com.cha.markit.config;

import com.cha.markit.domain.User;
import com.cha.markit.repository.UserRepository;
import com.cha.markit.util.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");
        String provider = extractProvider(authentication);

        // DB에서 사용자 찾거나 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .id(UUID.randomUUID().toString())
                                .email(email)
                                .name(name)
                                .provider(provider)
                                .providerId(providerId)
                                .build()
                ));

        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail());

        response.sendRedirect("http://localhost:8080/callback?token=" + token);
    }

    private String extractProvider(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            return ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        }

        return "unknown";
    }
}
