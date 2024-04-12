package org.vector.assistant.persistance.entity;

import java.time.OffsetDateTime;

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
@Table(name = "thread", schema = "chatbot")
@Builder
public class ThreadEntity implements Persistable<String> {

    @Id
    private String id;

    @Column("created_at")
    private OffsetDateTime createdAt;

    @Builder.Default
    @Transient
    private Boolean isNew = Boolean.FALSE;

    @Override
    public boolean isNew() {
        return isNew;
    }
}
