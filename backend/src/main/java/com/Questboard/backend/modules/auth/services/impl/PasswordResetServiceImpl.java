// package com.Questboard.backend.modules.auth.services.impl;

// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.util.UUID;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import com.Questboard.backend.modules.auth.dto.request.ResetPasswordRequest;
// import com.Questboard.backend.modules.auth.exception.AuthException;
// import com.Questboard.backend.modules.auth.model.User;
// import com.Questboard.backend.modules.auth.repository.RefreshTokenRepository;
// import com.Questboard.backend.modules.auth.repository.UserRepository;
// import com.Questboard.backend.modules.auth.services.PasswordResetService;

// import jakarta.transaction.Transactional;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class PasswordResetServiceImpl implements PasswordResetService {

//     @Autowired
//     private RedisTemplate<String, Object> redisTemplate;
//     private final RefreshTokenRepository tokenRepository;
//     private final UserRepository userRepository;
//     private final PasswordEncoder passwordEncoder;

//     // @Override
//     // public String createResetToken(User user) {

//     // // optionally invalidate old tokens first
//     // // tokenRepository.deleteByUser(user);

//     // PasswordResetToken token = PasswordResetToken.builder()
//     // .token(UUID.randomUUID().toString())
//     // .user(user)
//     // .expiryDate(LocalDateTime.now().plusMinutes(30))
//     // .build();

//     // tokenRepository.save(token);

//     // return token.getToken();
//     // }

//     @Override
//     public String createResetToken(User user) {

//         String token = UUID.randomUUID().toString();

//         String key = "password-reset:" + token;

//         redisTemplate.opsForValue().set(
//                 key,
//                 user.getId(), // store only userId (better than full object)
//                 Duration.ofMinutes(30));

//         return token;
//     }

//     // @Override
//     // public PasswordResetToken validateToken(String token) {

//     //     PasswordResetToken resetToken = tokenRepository.findByToken(token)
//     //             .orElseThrow(() -> new AuthException("Invalid reset token"));

//     //     if (resetToken.isExpired()) {
//     //         throw new AuthException("Reset token expired");
//     //     }

//     //     return resetToken;
//     // }

//     // @Override
//     // @Transactional
//     // public void resetPassword(ResetPasswordRequest request) {

//     //     PasswordResetToken resetToken = validateToken(request.token());

//     //     User user = resetToken.getUser();

//     //     user.setPassword(passwordEncoder.encode(request.newPassword()));

//     //     userRepository.save(user);

//     //     tokenRepository.delete(resetToken);
//     // }

//     // @Override
//     // public void invalidateToken(String token) {
//     //     tokenRepository.deleteByToken(token);
//     // }
// }
