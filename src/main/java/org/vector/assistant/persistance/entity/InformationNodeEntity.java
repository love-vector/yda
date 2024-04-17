package org.vector.assistant.persistance.entity;

import java.util.UUID;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "information_nodes", schema = "chatbot")
@Getter
@Builder(toBuilder = true)
public class InformationNodeEntity {

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("collection_name")
    private String collectionName;

    @Column("description")
    private String description;

    @Column("user_id")
    private UUID userId;
}
