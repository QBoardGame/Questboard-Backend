package com.Questboard.backend.modules.auth.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import lombok.*;

@Entity
@Table(name = "policy_acceptance")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyAcceptance {

    private static final String DEFAULT_TERMS_VERSION = "v1.0";
    private static final String DEFAULT_PRIVACY_VERSION = "v1.0";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "terms_accepted", nullable = false)
    private Boolean termsAccepted;

    @Column(name = "privacy_accepted", nullable = false)
    private Boolean privacyAccepted;

    @Column(name = "terms_version", nullable = false, length = 50)
    private String termsVersion;

    @Column(name = "privacy_version", nullable = false, length = 50)
    private String privacyVersion;

    @Column(name = "accepted_at", nullable = false)
    private LocalDateTime acceptedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void prePersist() {
        if (acceptedAt == null) {
            acceptedAt = LocalDateTime.now();
        }

        if (termsAccepted == null) {
            termsAccepted = Boolean.FALSE;
        }

        if (privacyAccepted == null) {
            privacyAccepted = Boolean.FALSE;
        }

        if (termsVersion == null || termsVersion.isBlank()) {
            termsVersion = DEFAULT_TERMS_VERSION;
        }

        if (privacyVersion == null || privacyVersion.isBlank()) {
            privacyVersion = DEFAULT_PRIVACY_VERSION;
        }
    }
}
