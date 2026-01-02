package su.ternovskii.tms;

import java.time.LocalDateTime;

public record Task(
        Long id,
        Long creatorId,
        Long assignedUserId,
        Status status,
        LocalDateTime createdDateTime,
        LocalDateTime deadlineDate,
        Priority priority
) {
}
