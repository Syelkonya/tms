package su.ternovskii.tms.domain.model;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record Task(
        Long id,

        @NotNull
        Long creatorId,
        Long assignedUserId,
        TaskStatus taskStatus,
        LocalDateTime createdDateTime,

        @NotNull
        @Future
        LocalDateTime deadlineDate,

        LocalDateTime doneDateTime,

        @NotNull
        TaskPriority taskPriority
) {
        public boolean isOverdue() {
                if (taskStatus == TaskStatus.DONE || taskStatus == TaskStatus.CANCELLED) {
                        return false;
                }
                return LocalDateTime.now().isAfter(deadlineDate);
        }

        public boolean canBeStarted() {
                return taskStatus == TaskStatus.CREATED || taskStatus == TaskStatus.ASSIGNED;
        }

        public boolean canBeCompleted() {
                return taskStatus == TaskStatus.IN_PROGRESS || taskStatus == TaskStatus.IN_REVIEW;
        }

        public Task assignTo(Long userId) {
                if (userId == null) {
                        throw new IllegalArgumentException("User ID cannot be null");
                }
                return new Task(
                        id, creatorId, userId, TaskStatus.ASSIGNED,
                        createdDateTime, deadlineDate, doneDateTime, taskPriority
                );
        }

        public Task start() {
                if (!canBeStarted()) {
                        throw new IllegalStateException("Task cannot be started in status: " + taskStatus);
                }
                return new Task(
                        id, creatorId, assignedUserId, TaskStatus.IN_PROGRESS,
                        createdDateTime, deadlineDate, doneDateTime, taskPriority
                );
        }

        public Task complete() {
                if (!canBeCompleted()) {
                        throw new IllegalStateException("Task cannot be completed in status: " + taskStatus);
                }
                return new Task(
                        id, creatorId, assignedUserId, TaskStatus.DONE,
                        createdDateTime, deadlineDate, LocalDateTime.now(), taskPriority
                );
        }
        
}
