package ai.yda.llm.thread;

import java.time.OffsetDateTime;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "threads", indexes = @Index(name = "threads_assistant_id", columnList = "assistant_id"))
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class ThreadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "thread_id", nullable = false, unique = true)
    private String threadId;

    @Column(name = "assistant_id", length = 36, nullable = false)
    private Long assistantId;
}
