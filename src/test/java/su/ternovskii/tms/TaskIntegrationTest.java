package su.ternovskii.tms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class TaskIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TaskService taskService;

    @Test
    void shouldCreateAndRetrieveTask() {
        // Given
        Task task = new Task(
                null,
                1L,                              // creatorId
                null,                            // assignedUserId
                null,                            // status (will be set to CREATED)
                LocalDateTime.now(),             // createdDateTime
                LocalDateTime.now().plusDays(7), // deadlineDate
                null,                            // doneDateTime
                Priority.HIGH                      // priority
        );

        // When
        Task created = taskService.createTask(task);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.status()).isEqualTo(Status.CREATED);
        assertThat(created.priority()).isEqualTo(Priority.HIGH);

        // Verify we can retrieve it
        Task retrieved = taskService.getTaskById(created.id());
        assertThat(retrieved.id()).isEqualTo(created.id());
    }
}