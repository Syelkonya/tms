package su.ternovskii.tms;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task getTaskById(long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task with id " + id));
        return mapToDomainTask(taskEntity);
    }

    public List<Task> getAllTasks() {
        List<TaskEntity> taskEntity = taskRepository.findAll();

        return taskEntity.stream().map(this::mapToDomainTask).toList();
    }

    public Task createTask(Task taskToCreate) {
        if (taskToCreate.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (taskToCreate.status() != null) {
            throw new IllegalArgumentException("Start date must be 1 day earlier than end date");
        }
//        db auto insert id
        var taskToSave = new TaskEntity(
                null,
                taskToCreate.creatorId(),
                taskToCreate.assignedUserId(),
                Status.CREATED,
                taskToCreate.createdDateTime(),
                taskToCreate.deadlineDate(),
                taskToCreate.doneDateTime(),
                taskToCreate.priority()
        );
        var savedTaskEntity = taskRepository.save(taskToSave);

        return mapToDomainTask(savedTaskEntity);
    }

    public Task update(Long id, Task taskToUpdate) {
        var taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No task with id " + id));
        if (taskEntity.getStatus() == Status.DONE && taskToUpdate.status() != Status.IN_PROGRESS) {
            throw new IllegalStateException("The task with id " + id + " is in status DONE " +
                    "& new status will be not IN PROGRESS");
        }
        var taskToSave = new TaskEntity(
                taskEntity.getId(),
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                taskToUpdate.status(),
                taskToUpdate.createdDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.doneDateTime(),
                taskToUpdate.priority()
        );
        var updatedTask = taskRepository.save(taskToSave);
        return mapToDomainTask(updatedTask);
    }

    public void deleteTask(Long id) {
        taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No task with id " + id));
        taskRepository.deleteById(id);
    }


    public Task startTask(Long id) {
        var taskToStart = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No task with id " + id));
        if (taskToStart.getAssignedUserId() == null) {
            throw new IllegalArgumentException("Assigned user id must be exist");
        }
        if (taskRepository.findAllByAssignedUserIdAndStatus(taskToStart.getAssignedUserId(), Status.IN_PROGRESS).size() > 4) {
            throw new IllegalArgumentException("Assigned user has more than 4 tasks in status IN_PROGRESS");
        }
       taskToStart.setStatus(Status.IN_PROGRESS);
       var startedTask = taskRepository.save(taskToStart);
        log.info("succeed delete task id {}", id);
       return mapToDomainTask(startedTask);
    }

    public Task completeTask(Long id) {
        var taskToComplete = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No task with id " + id));
        if (taskToComplete.getAssignedUserId() == null) {
            throw new IllegalArgumentException("Assigned user id must be exist");
        }
        if (taskToComplete.getDeadlineDate() == null) {
            throw new IllegalArgumentException("Deadline Date must be exist");
        }
        taskToComplete.setDoneDateTime(LocalDateTime.now());
        taskToComplete.setStatus(Status.DONE);
        var completedTask = taskRepository.save(taskToComplete);
        log.info("succeed complete task with id {}", id);
        return mapToDomainTask(completedTask);
    }

    private Task mapToDomainTask(TaskEntity taskEntity) {
        return new Task(
                taskEntity.getId(),
                taskEntity.getCreatorId(),
                taskEntity.getAssignedUserId(),
                taskEntity.getStatus(),
                taskEntity.getCreatedDateTime(),
                taskEntity.getDeadlineDate(),
                taskEntity.getDoneDateTime(),
                taskEntity.getPriority()
        );
    }

}
