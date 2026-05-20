// package com.Questboard.backend.modules.challenges.mapper;

// import com.Questboard.backend.modules.challenges.dto.ChallengeDefinitionDto;
// import com.Questboard.backend.modules.challenges.dto.UserChallengeDto;
// import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
// import com.Questboard.backend.modules.challenges.entity.UserChallenge;

// public class ChallengeMapper {
//     public static ChallengeDefinitionDto toDto(ChallengeDefinition def) {
//         if (def == null) return null;
//         return ChallengeDefinitionDto.builder()
//                 .id(def.getId())
//                 .gameId(def.getGameId())
//                 .title(def.getTitle())
//                 .description(def.getDescription())
//                 .challengeType(def.getChallengeType())
//                 .eventType(def.getEventType())
//                 .targetValue(def.getTargetValue())
//                 .rewardAmount(def.getRewardValue())
//                 .rarityWeight(def.getRarityWeight())
//                 .createdAt(def.getCreatedAt())
//                 .updatedAt(def.getUpdatedAt())
//                 .build();
//     }

//     public static UserChallengeDto toDto(UserChallenge uc) {
//         if (uc == null) return null;
//         return UserChallengeDto.builder()
//                 .id(uc.getId())
//                 .challengeDefinitionId(uc.getChallengeDefinitionId())
//                 .progress(uc.getProgress())
//                 .completed(uc.isCompleted())
//                 .claimed(uc.isClaimed())
//                 .expiresAt(uc.getExpiresAt())
//                 .completedAt(uc.getCompletedAt())
//                 .createdAt(uc.getCreatedAt())
//                 .build();
//     }
// }

package com.Questboard.backend.modules.challenges.mapper;

import com.Questboard.backend.modules.challenges.dto.ChallengeDefinitionDto;
import com.Questboard.backend.modules.challenges.dto.UserChallengeProgressDto;
import com.Questboard.backend.modules.challenges.entity.ChallengeDefinition;
import com.Questboard.backend.modules.challenges.entity.UserChallengeProgress;

public class ChallengeMapper {

    private ChallengeMapper() {
    }

    public static ChallengeDefinitionDto toDto(ChallengeDefinition def) {

        if (def == null) {
            return null;
        }

        return ChallengeDefinitionDto.builder()
                .id(def.getId())
                .gameId(def.getGameId())

                // Creator Info
                .createdBy(def.getCreatedBy())
                .creatorType(def.getCreatorType())

                // Basic Info
                .title(def.getTitle())
                .description(def.getDescription())

                // Challenge Info
                .challengeType(def.getChallengeType())
                .eventType(def.getEventType())
                .targetValue(def.getTargetValue())
                .conditions(def.getConditions())

                // Reward Info
                .rewardType(def.getRewardType())
                .rewardValue(def.getRewardValue())

                // Scheduling
                .startsAt(def.getStartsAt())
                .endsAt(def.getEndsAt())

                // Visibility + Status
                .visibility(def.getVisibility())
                .status(def.getStatus())

                // Metadata
                .rarityWeight(def.getRarityWeight())
                .featured(def.getFeatured())

                // Timestamps
                .createdAt(def.getCreatedAt())
                .updatedAt(def.getUpdatedAt())

                .build();
    }

    // public static UserChallengeProgress toDto(
    //         UserChallengeProgress progress) {

    //     if (progress == null) {
    //         return null;
    //     }

    //     return UserChallengeProgress.builder()
    //             .id(progress.getId())
    //             .userId(progress.getUserId())
    //             .challengeId(progress.getChallengeId())

    //             // Progress
    //             .progress(progress.getProgress())
    //             .targetValue(progress.getTargetValue())

    //             // Completion
    //             .completed(progress.isCompleted())
    //             .claimed(progress.isClaimed())
    //             .completedAt(progress.getCompletedAt())

    //             // Metadata
    //             .updatedAt(progress.getUpdatedAt())

    //             .build();
    // }

    public static UserChallengeProgressDto toDto(UserChallengeProgress uc) {

        if (uc == null)
            return null;

        return UserChallengeProgressDto.builder()
                .id(uc.getId())
                .userId(uc.getUserId())
                .challengeId(uc.getChallengeId())
                .progress(uc.getProgress())
                .targetValue(uc.getTargetValue())
                .completed(uc.isCompleted())
                .claimed(uc.isClaimed())
                .completedAt(uc.getCompletedAt())
                .updatedAt(uc.getUpdatedAt())
                .build();
    }

}
