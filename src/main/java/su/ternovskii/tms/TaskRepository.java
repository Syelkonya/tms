package su.ternovskii.tms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query("select t from TaskEntity t where t.assignedUserId = :assignedUserId")
    List<TaskEntity> findAllByAssignedUserId(Long assignedUserId);

    List<TaskEntity> findAllByAssignedUserIdAndStatus(Long assignedUserId, Status status);
}
