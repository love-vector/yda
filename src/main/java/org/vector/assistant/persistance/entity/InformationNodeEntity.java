package org.vector.assistant.persistance.entity;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "information_node", schema = "chatbot")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class InformationNodeEntity implements Persistable<UUID> {

    @Id
    @Column("id")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column("name")
    private String name;

    @Column("description")
    private String description;

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
