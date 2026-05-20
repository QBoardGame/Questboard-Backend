package com.Questboard.backend.modules.challenges.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeWithProgressDto {

    private ChallengeDefinitionDto challenge;

    private UserChallengeProgressDto progress;

}
