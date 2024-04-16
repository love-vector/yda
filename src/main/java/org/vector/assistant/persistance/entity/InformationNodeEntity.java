package org.vector.assistant.persistance.entity;

import java.util.UUID;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "information_nodes")
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class InformationNodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "collection_name")
    private String collectionName;

    @Column(name = "description")
    private String description;

    @Column(name = "user_id")
    private UUID userId;
}
