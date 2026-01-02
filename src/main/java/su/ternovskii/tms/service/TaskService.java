package su.ternovskii.tms.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import su.ternovskii.tms.Status;
import su.ternovskii.tms.Task;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    private final HashMap<Long, Task> taskMap;
    private final AtomicLong counter;

    public TaskService() {
        this.taskMap = new HashMap<>();
        this.counter = new AtomicLong();
    }

    public Task getTaskById(long id){
        if(!taskMap.containsKey(id)) {
            throw new NoSuchElementException("Not found task with id " + id);
        }
        return taskMap.get(id);
    }

    public List<Task> getAllTasks(){
        return taskMap.values().stream().toList();
    }

    public Task createTask(Task taskToCreate) {
        if (taskToCreate.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (taskToCreate.status() != null) {
            throw new IllegalArgumentException("Start date must be 1 day earlier than end date");
        }
        var newTask  = new Task(
                counter.incrementAndGet(),
                taskToCreate.creatorId(),
                taskToCreate.assignedUserId(),
                Status.CREATED,
                taskToCreate.createdDateTime(),
                taskToCreate.deadlineDate(),
                taskToCreate.priority()
        );
        taskMap.put(newTask.id(), newTask);
        return newTask;
    }

    public Task update(Long id, Task taskToUpdate) {
        if (taskMap.get(id) == null) {
            throw new NoSuchElementException("No task with id " + id);
        }
        if (taskMap.get(id).status().equals(Status.DONE)){
            throw new IllegalStateException("The task with id " + id + " is in status done");
        }
        var updatedTask  = new Task(
                id,
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                taskToUpdate.status(),
                taskToUpdate.createdDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.priority()
        );
        taskMap.put(id, updatedTask);
        return updatedTask;
    }

    public void deleteTask(Long id) {
        if (taskMap.get(id) == null) {
            throw new NoSuchElementException("No task with id " + id);
        }
        taskMap.remove(id);
    }
}
