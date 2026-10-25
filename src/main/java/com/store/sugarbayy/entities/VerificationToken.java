package com.store.sugarbayy.entities;

import com.store.sugarbayy.enums.VerificationTokenChannel;
import com.store.sugarbayy.enums.VerificationTokenType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "verification_token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel", nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationTokenChannel channel;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationTokenType type;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    @Column(name = "code_hash", nullable = false)
    private String codeHash;

    @Column(name = "expiry", nullable = false)
    private Instant expiry;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "manual_revoke", nullable = false)
    @Builder.Default
    private Boolean manualRevoke = false;
}
