// package com.Questboard.backend.modules.auth.services;

// import java.io.IOException;
// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;

// import org.springframework.security.core.Authentication;
// import org.springframework.security.oauth2.core.user.OAuth2User;
// import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
// import org.springframework.stereotype.Component;

// import com.Questboard.backend.modules.auth.dto.TokenPair;
// import com.Questboard.backend.modules.auth.model.User;
// import com.Questboard.backend.modules.auth.repository.UserRepository;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class OAuth2SuccessHandler
//         extends SimpleUrlAuthenticationSuccessHandler {

//     private final UserRepository userRepository;
//     private final TokenService tokenService;

//     @Override
//     public void onAuthenticationSuccess(
//             HttpServletRequest request,
//             HttpServletResponse response,
//             Authentication authentication)
//             throws IOException {

//         OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

//         String email = oauthUser.getAttribute("email");

//         User user = userRepository.findByEmail(email)
//                 .orElseThrow();

//         TokenPair tokens = tokenService.createTokens(user);

//         String redirect = request.getParameter("redirect");

//         getRedirectStrategy().sendRedirect(
//                 request,
//                 response,
//                 redirect
//                         + "?accessToken="
//                         + URLEncoder.encode(
//                                 tokens.accessToken(),
//                                 StandardCharsets.UTF_8)
//                         + "&refreshToken="
//                         + URLEncoder.encode(
//                                 tokens.refreshToken(),
//                                 StandardCharsets.UTF_8));
//     }
// }