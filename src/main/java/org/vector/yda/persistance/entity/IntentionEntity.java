package org.vector.yda.persistance.entity;

import java.util.UUID;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "intentions")
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class IntentionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "definition")
    private String definition;

    @Column(name = "description")
    private String description;

    @Column(name = "vector_id")
    private UUID vectorId;
}
