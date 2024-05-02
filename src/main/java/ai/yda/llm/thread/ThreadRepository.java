package ai.yda.llm.thread;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends JpaRepository<ThreadEntity, Long> {

    List<ThreadEntity> findAllByAssistantId(final Long assistantId);
}
