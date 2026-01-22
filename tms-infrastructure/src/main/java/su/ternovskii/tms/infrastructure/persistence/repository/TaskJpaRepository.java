package su.ternovskii.tms.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import su.ternovskii.tms.domain.model.TaskStatus;
import su.ternovskii.tms.infrastructure.persistence.entity.TaskEntity;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {

    @Query("select t from TaskEntity t where t.assignedUserId = :assignedUserId")
    List<TaskEntity> findAllByAssignedUserId(Long assignedUserId);

    List<TaskEntity> findAllByAssignedUserIdAndStatus(Long assignedUserId, TaskStatus status);
}
