// package com.Questboard.backend.modules.challenges.service.impl;

// import com.Questboard.backend.modules.challenges.dto.UserChallengeDto;
// import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
// import com.Questboard.backend.modules.challenges.entity.UserChallenge;
// import com.Questboard.backend.modules.challenges.mapper.ChallengeMapper;
// import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;
// import com.Questboard.backend.modules.challenges.repository.UserChallengeRepository;
// import com.Questboard.backend.modules.challenges.service.ChallengeService;
// import com.Questboard.backend.modules.challenges.service.RewardService;
// import com.Questboard.backend.modules.challenges.exception.NotFoundException;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.Instant;
// import java.time.LocalDate;
// import java.time.ZoneOffset;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;
// import java.util.stream.Collectors;

// @Service
// public class ChallengeServiceImpl implements ChallengeService {
//     private final UserChallengeRepository userChallengeRepository;
//     private final ChallengeDefinitionRepository challengeDefinitionRepository;
//     private final RewardService rewardService;

//     public ChallengeServiceImpl(UserChallengeRepository userChallengeRepository,
//                                 ChallengeDefinitionRepository challengeDefinitionRepository,
//                                 RewardService rewardService) {
//         this.userChallengeRepository = userChallengeRepository;
//         this.challengeDefinitionRepository = challengeDefinitionRepository;
//         this.rewardService = rewardService;
//     }

//     @Override
//     public Page<UserChallengeDto> getUserChallenges(UUID userId, Pageable pageable) {
//         Page<UserChallenge> page = userChallengeRepository.findByUserIdAndCompleted(userId, false, pageable);
//         List<UserChallengeDto> dtos = page.stream().map(ChallengeMapper::toDto).collect(Collectors.toList());
//         return new PageImpl<>(dtos, pageable, page.getTotalElements());
//     }

//     @Override
//     @Transactional
//     public void assignChallengeToUser(UUID userId, UUID challengeDefinitionId) {
//         ChallengeDefinition def = challengeDefinitionRepository.findById(challengeDefinitionId)
//                 .orElseThrow(() -> new NotFoundException("ChallengeDefinition not found"));

//         UserChallenge uc = UserChallenge.builder()
//                 .id(UUID.randomUUID())
//                 .userId(userId)
//                 .challengeDefinitionId(def.getId())
//                 .progress(0)
//                 .completed(false)
//                 .claimed(false)
//                 .createdAt(Instant.now())
//                 .gameId(def.getGameId())
//                 .eventType(def.getEventType())
//                 .targetValue(def.getTargetValue() == null ? null : def.getTargetValue().intValue())
//                 .rewardAmount(def.getRewardAmount() == null ? null : def.getRewardAmount().toString())
//                 .build();

//         if (def.getChallengeType() != null && def.getChallengeType().name().equals("DAILY")) {
//             LocalDate tomorrow = LocalDate.now(ZoneOffset.UTC).plusDays(1);
//             uc.setExpiresAt(tomorrow.atStartOfDay().toInstant(ZoneOffset.UTC));
//         }

//         userChallengeRepository.save(uc);
//     }

//     @Override
//     @Transactional
//     public void claimReward(UUID userId, UUID userChallengeId) {
//         Optional<UserChallenge> opt = userChallengeRepository.findById(userChallengeId);
//         if (opt.isEmpty()) throw new NotFoundException("UserChallenge not found");
//         UserChallenge uc = opt.get();
//         if (!uc.getUserId().equals(userId)) throw new IllegalArgumentException("Not your challenge");
//         if (!uc.isCompleted()) throw new IllegalStateException("Challenge not completed");
//         if (uc.isClaimed()) return;

//         rewardService.grantReward(userId, uc.getRewardAmount() == null ? "" : uc.getRewardAmount().toString(), uc.getId());
//         uc.setClaimed(true);
//         userChallengeRepository.save(uc);
//     }
// }

package com.Questboard.backend.modules.challenges.service.impl;

import com.Questboard.backend.modules.challenges.dto.ChallengeCompletedEvent;
import com.Questboard.backend.modules.challenges.dto.ChallengeWithProgressDto;
import com.Questboard.backend.modules.challenges.dto.ParticipationResponse;
import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.entity.ParticipationStatus;
import com.Questboard.backend.modules.challenges.entity.UserChallengeParticipation;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;
import com.Questboard.backend.modules.challenges.enums.ChallengeStatus;
import com.Questboard.backend.modules.challenges.mapper.ChallengeMapper;
import com.Questboard.backend.modules.challenges.repository.ChallengeDefinitionRepository;
import com.Questboard.backend.modules.challenges.repository.UserChallengeParticipationRepository;
import com.Questboard.backend.modules.challenges.repository.UserChallengeProgressRepository;
import com.Questboard.backend.modules.challenges.service.ChallengeService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeServiceImpl implements ChallengeService {

        private final ChallengeDefinitionRepository challengeRepository;

        private final UserChallengeProgressRepository progressRepository;

        private final UserChallengeParticipationRepository participationRepo;

        private final ApplicationEventPublisher eventPublisher;

        @Override
        public List<ChallengeWithProgressDto> getActiveChallenges(
                        Long gameId,
                        UUID userId) {

                List<ChallengeDefinition> challenges = challengeRepository.findActiveChallenges(
                                gameId,
                                ChallengeStatus.ACTIVE,
                                Instant.now());

                return challenges.stream()
                                .map(challenge -> buildChallengeWithProgress(challenge, userId))
                                .toList();
        }

        @Override
        public void claimReward(
                        UUID userId,
                        UUID challengeId) {

                UserChallengeProgress progress = progressRepository
                                .findByUserIdAndChallengeId(
                                                userId,
                                                challengeId)
                                .orElseThrow();

                if (!progress.isCompleted()) {
                        throw new RuntimeException(
                                        "Challenge not completed");
                }

                if (progress.isClaimed()) {
                        throw new RuntimeException(
                                        "Reward already claimed");
                }

                progress.setClaimed(true);

                progressRepository.save(progress);
        }

        private ChallengeWithProgressDto buildChallengeWithProgress(
                        ChallengeDefinition challenge,
                        UUID userId) {

                UserChallengeProgress progress = progressRepository
                                .findByUserIdAndChallengeId(userId, challenge.getId())
                                .orElse(null);

                return ChallengeWithProgressDto.builder()
                                .challenge(ChallengeMapper.toDto(challenge))
                                .progress(
                                                progress != null
                                                                ? ChallengeMapper.toDto(progress)
                                                                : null)
                                .build();
        }

        // @Transactional
        // public void updateProgressAndMaybePublishEvent(
        // UserChallengeProgress progress,
        // ChallengeDefinition challenge,
        // Long incrementValue) {

        // long updatedProgress = progress.getProgress() + incrementValue;

        // progress.setProgress(updatedProgress);

        // boolean justCompleted = !progress.isCompleted()
        // && updatedProgress >= challenge.getTargetValue()
        // && !isExpired(progress);

        // if (justCompleted) {

        // progress.setCompleted(true);
        // progress.setCompletedAt(Instant.now());

        // progressRepository.save(progress);

        // // 🔥 DOMAIN EVENT (ONLY ON FIRST COMPLETION)
        // eventPublisher.publishEvent(
        // new ChallengeCompletedEvent(
        // progress.getUserId(),
        // challenge.getId(),
        // challenge.getGameId(),
        // progress.getId(),
        // challenge.getRewardValue()));

        // return;
        // }

        // progressRepository.save(progress);
        // }

        public void updateProgressAndMaybePublishEvent(
                        UserChallengeProgress progress,
                        ChallengeDefinition challenge,
                        Long incrementValue) {

                log.info("➡️ Entered updateProgressAndMaybePublishEvent | userId={} | progressId={} | increment={}",
                                progress.getUserId(),
                                progress.getId(),
                                incrementValue);

                long updatedProgress = progress.getProgress() + incrementValue;

                log.debug("Current progress={} | Updated progress={}",
                                progress.getProgress(),
                                updatedProgress);

                progress.setProgress(updatedProgress);

                boolean justCompleted = !progress.isCompleted()
                                && updatedProgress >= challenge.getTargetValue()
                                && !isExpired(progress);

                log.debug("Completion check | justCompleted={} | target={} | expired={}",
                                justCompleted,
                                challenge.getTargetValue(),
                                isExpired(progress));

                if (justCompleted) {

                        log.info("🎯 Challenge just completed | userId={} | challengeId={} | progressId={}",
                                        progress.getUserId(),
                                        challenge.getId(),
                                        progress.getId());

                        progress.setCompleted(true);
                        progress.setCompletedAt(Instant.now());

                        progressRepository.save(progress);

                        log.info("💾 Progress saved as completed");

                        eventPublisher.publishEvent(
                                        new ChallengeCompletedEvent(
                                                        progress.getUserId(),
                                                        challenge.getId(),
                                                        challenge.getGameId(),
                                                        progress.getId(),
                                                        challenge.getRewardValue()));

                        log.info("📣 ChallengeCompletedEvent published");

                        return;
                }

                progressRepository.save(progress);

                log.info("💾 Progress saved (not completed) | progress={}", updatedProgress);
        }

        private boolean isExpired(UserChallengeProgress progress) {
                return progress.getExpiresAt() != null
                                && Instant.now().isAfter(progress.getExpiresAt());
        }

        @Override
        @Transactional
        public ParticipationResponse joinChallenge(UUID userId, UUID challengeId) {

                ChallengeDefinition challenge = challengeRepository.findById(challengeId)
                                .orElseThrow(() -> new RuntimeException("Challenge not found"));

                // 1. Already joined check
                boolean exists = participationRepo
                                .existsByUserIdAndChallengeIdAndStatus(
                                                userId,
                                                challengeId,
                                                ParticipationStatus.ACTIVE);

                if (exists) {
                        return ParticipationResponse.alreadyJoined(challengeId, userId);
                }

                // 2. Capacity check
                if (challenge.getTotalPlayersAllowed() != null) {

                        long count = participationRepo
                                        .countByChallengeIdAndStatus(challengeId, ParticipationStatus.ACTIVE);

                        if (count >= challenge.getTotalPlayersAllowed()) {
                                throw new RuntimeException("CHALLENGE_FULL");
                        }
                }

                // 3. Save participation
                UserChallengeParticipation participation = UserChallengeParticipation.builder()
                                .id(UUID.randomUUID())
                                .userId(userId)
                                .challengeId(challengeId)
                                .gameId(challenge.getGameId())
                                .status(ParticipationStatus.ACTIVE)
                                .joinedAt(Instant.now())
                                .build();

                participationRepo.save(participation);

                long updatedCount = participationRepo
                                .countByChallengeIdAndStatus(challengeId, ParticipationStatus.ACTIVE);

                return ParticipationResponse.builder()
                                .challengeId(challengeId)
                                .userId(userId)
                                .status("JOINED")
                                .joinedAt(participation.getJoinedAt())
                                .currentParticipants(updatedCount)
                                .maxParticipants(challenge.getTotalPlayersAllowed())
                                .build();
        }
}
