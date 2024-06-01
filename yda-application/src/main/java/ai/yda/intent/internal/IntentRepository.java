package ai.yda.intent.internal;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntentRepository extends JpaRepository<IntentEntity, Long> {

    Optional<IntentEntity> findByVectorId(final UUID vectorId);
}
