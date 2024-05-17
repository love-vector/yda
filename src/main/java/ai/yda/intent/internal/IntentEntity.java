package ai.yda.intent.internal;

import java.util.UUID;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "intents")
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class IntentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", length = 80, nullable = false, unique = true)
    private String name;

    @Column(name = "definition", length = 400, nullable = false, unique = true)
    private String definition;

    @Column(name = "description")
    private String description;

    @Column(name = "vector_id", nullable = false, unique = true)
    private UUID vectorId;
}
