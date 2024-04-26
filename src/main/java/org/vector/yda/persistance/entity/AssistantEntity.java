package org.vector.yda.persistance.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(
        name = "assistants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}),
        indexes = @Index(name = "assistants_user_id", columnList = "user_id"))
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class AssistantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "instructions", nullable = false)
    private String instructions;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "assistant_id", length = 36, nullable = false, unique = true)
    private String assistantId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
