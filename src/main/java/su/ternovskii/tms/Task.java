package su.ternovskii.tms;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record Task(
        Long id,

        @NotNull
        Long creatorId,
        Long assignedUserId,
        Status status,
        LocalDateTime createdDateTime,

        @NotNull
        @Future
        LocalDateTime deadlineDate,

        LocalDateTime doneDateTime,

        @NotNull
        Priority priority
) {
}
