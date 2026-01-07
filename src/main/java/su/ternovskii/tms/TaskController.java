package su.ternovskii.tms;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        log.info("method getAllTasks starts");
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTasksById(
            @PathVariable Long id
    ) {
        log.info("method getTasksById starts with id - {}", id);
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping()
    public ResponseEntity<Task> createTask(
            @RequestBody Task taskToCreate
    ) {
        log.info("called create task {}", taskToCreate);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(taskToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody Task taskToUpdate
    ) {
        log.info("called update task id {} - {}", id, taskToUpdate);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.update(id, taskToUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id
    ) {
        log.info("called delete task id {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Task> startTaskById(
            @PathVariable Long id
    ) {
        log.info("called start task id {}", id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.startTask(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Task> completeTaskById(
            @PathVariable Long id
    ) {
        log.info("called complete task id {}", id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.completeTask(id));
    }
}
