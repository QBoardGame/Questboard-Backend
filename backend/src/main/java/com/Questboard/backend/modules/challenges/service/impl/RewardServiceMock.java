// package com.Questboard.backend.modules.challenges.service.impl;

// import com.Questboard.backend.modules.challenges.enums.RewardType;
// import com.Questboard.backend.modules.challenges.service.RewardService;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Service;

// import java.util.UUID;

// @Service
// public class RewardServiceMock implements RewardService {
//     private final Logger log = LoggerFactory.getLogger(RewardServiceMock.class);

//     @Override
//     public void grantReward(UUID userId, RewardType rewardType, String amount, UUID userChallengeId) {
//         // Mock integration with wallet/payment. In prod, implement real integration.
//         log.info("Granting reward {} to user {} for challenge {}", amount, userId, userChallengeId);
//     }
// }
