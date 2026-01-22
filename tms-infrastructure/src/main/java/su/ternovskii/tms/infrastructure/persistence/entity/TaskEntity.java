package su.ternovskii.tms.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import su.ternovskii.tms.domain.model.TaskPriority;
import su.ternovskii.tms.domain.model.TaskStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "assigned_user_id")
    private Long assignedUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus taskStatus;

    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;

    @Column(name = "deadline_date")
    private LocalDateTime deadlineDate;

    @Column(name = "done_date_time")
    private LocalDateTime doneDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private TaskPriority taskPriority;
}
