package org.vector.assistant.persistance.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assistant", schema = "chatbot")
@Builder
public class AssistantEntity implements Persistable<String> {

    @Id
    private String id;

    @Column("name")
    private String name;

    @Column("instructions")
    private String instructions;

    @Column("created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column("user_id")
    private UUID userId;

    @Builder.Default
    @Transient
    private Boolean isNew = Boolean.FALSE;

    @Override
    public boolean isNew() {
        return isNew;
    }
}
