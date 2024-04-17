package org.vector.assistant.persistance.entity;

import java.util.UUID;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "intentions", schema = "chatbot")
@Getter
@Builder(toBuilder = true)
public class IntentionEntity {

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("definition")
    private String definition;

    @Column("description")
    private String description;

    @Column("vector_id")
    private UUID vectorId;
}
