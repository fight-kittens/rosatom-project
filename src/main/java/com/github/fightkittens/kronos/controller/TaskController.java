package com.github.fightkittens.kronos.controller;

import com.github.fightkittens.kronos.entities.Task;
import com.github.fightkittens.kronos.model.ErrorResponse;
import com.github.fightkittens.kronos.model.TaskArray;
import com.github.fightkittens.kronos.model.TaskModel;
import com.github.fightkittens.kronos.model.TaskResponse;
import com.github.fightkittens.kronos.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    TaskRepository repository;

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> getById(@PathVariable int id) {
        if (id <= 0) {
            return new ResponseEntity<>(new ErrorResponse(String.format("Task id must be a positive integer", id)),
                HttpStatus.BAD_REQUEST);
        }
        Task task = repository.findById(id).orElse(null);
        if (task != null) {
            TaskModel model = new TaskModel(task);
            return new ResponseEntity<>(model, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse(String.format("Task with id %d not found", id)),
                HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<? extends TaskResponse> deleteById(@PathVariable int id) {
        if (id <= 0) {
            return new ResponseEntity<>(new ErrorResponse(String.format("Task id must be a positive integer", id)),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            Task task = repository.findById(id).orElse(null);
            if (task == null) {
                return new ResponseEntity<>(new ErrorResponse(String.format("Task with id %d not found", id)),
                        HttpStatus.NOT_FOUND);
            }
            if (task.getParent() != null) {
                task.getParent().getChildren().remove(task);
                repository.save(task.getParent());
            }
            for (Task child : task.getChildren()) {
                child.setParent(null); //вопрос - удаляем ли и детей рекуррентно
                repository.save(child);
            }
            repository.deleteById(id);
            repository.flush();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("Some exception"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> updateById(@PathVariable int id, @RequestBody TaskModel model) {
        if (id <= 0) {
            return new ResponseEntity<>(new ErrorResponse(String.format("Task id must be a positive integer", id)),
                    HttpStatus.BAD_REQUEST);
        }
        Task task = repository.findById(id).orElse(null);
        if (task != null) {
            task.setName(model.getName());
            task.setDuration(model.getDuration());
            task.setMinDuration(model.getMinDuration());
            task.setReduceDurationPrice(model.getReduceDurationPrice());
            task.setShiftEarlierPrice(model.getShiftEarlierPrice());
            task.setShiftLaterPrice(model.getShiftLaterPrice());

            Task parentTask;
            if (model.getParentTask() == null) {
                parentTask = null;
            } else {
                parentTask = repository.findById(model.getParentTask()).orElse(null);
            }
            task.setParent(parentTask);
            if (parentTask != null) {
                repository.save(parentTask);
            }
            for (Integer childId : model.getChildren()) {
                Task childTask = repository.findById(childId).orElse(null);
                if (childTask == null) {
                    return new ResponseEntity<>(new ErrorResponse(String.format("Child with id %d not found", childId)), HttpStatus.BAD_REQUEST);
                } else {
                    task.addChild(childTask);
                    childTask.setParent(task);
                    repository.save(childTask);
                }
            }
            for (Integer connId : model.getConnected()) {
                Task connectedTask = repository.findById(connId).orElse(null);
                if (connectedTask == null) {
                    return new ResponseEntity<>(new ErrorResponse(String.format("Connected task with id %d not found", connId)), HttpStatus.BAD_REQUEST);
                } else {
                    task.addConnected(connectedTask);
                    connectedTask.addConnected(task);
                    repository.save(connectedTask);
                }
            }
            repository.saveAndFlush(task);
            return new ResponseEntity<>(new TaskModel(task), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse(String.format("Task with id %d not found", id)),
                HttpStatus.NOT_FOUND);
    }

    @PostMapping(consumes = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> addTask(@RequestBody TaskModel model) {
        try {
            Task parentTask;
            if (model.getParentTask() == null) {
                parentTask = null;
            } else {
                parentTask = repository.findById(model.getParentTask()).orElse(null);
            }
            Task task = new Task(model, parentTask);
            repository.save(task);
            if (parentTask != null) {
                repository.save(parentTask);
            }
            for (Integer id : model.getChildren()) {
                Task childTask = repository.findById(id).orElse(null);
                if (childTask == null) {
                    return new ResponseEntity<>(new ErrorResponse(String.format("Child with id %d not found", id)), HttpStatus.BAD_REQUEST);
                } else {
                    task.addChild(childTask);
                    childTask.setParent(task);
                    repository.save(childTask);
                }
            }
            for (Integer id : model.getConnected()) {
                Task connectedTask = repository.findById(id).orElse(null);
                if (connectedTask == null) {
                    return new ResponseEntity<>(new ErrorResponse(String.format("Connected task with id %d not found", id)), HttpStatus.BAD_REQUEST);
                } else {
                    task.addConnected(connectedTask);
                    connectedTask.addConnected(task);
                    repository.save(connectedTask);
                }
            }
            repository.saveAndFlush(task);
            return new ResponseEntity<>(new TaskModel(task), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("Exception: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "/connected/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> getConnectedById(@PathVariable int id) {
        if (id <= 0) {
            return new ResponseEntity<>(new ErrorResponse(String.format("Task id must be a positive integer", id)),
                    HttpStatus.BAD_REQUEST);
        }
        Task task = repository.findById(id).orElse(null);
        if (task != null) {
            TaskModel model = new TaskModel(task);
            TaskArray connected = new TaskArray(model.getConnected());
            return new ResponseEntity<>(connected, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse(String.format("Task with id %d not found", id)),
                HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/connected/batch", produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> getConnectedById(@RequestBody TaskArray ids) {
        Set<Integer> result = new LinkedHashSet<>();
        Set<Integer> tasks = ids.getTasks();
        for (Integer id : tasks) {
            if (id <= 0) {
                return new ResponseEntity<>(new ErrorResponse(String.format("Task id must be a positive integer", id)),
                        HttpStatus.BAD_REQUEST);
            }
            Task task = repository.findById(id).orElse(null);
            if (task != null) {
                TaskModel model = new TaskModel(task);
                result.addAll(model.getConnected());
            } else {
                return new ResponseEntity<>(new ErrorResponse(String.format("Task with id %d not found", id)),
                        HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(new TaskArray(result), HttpStatus.OK);
    }

    @GetMapping(value = "/children/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> getChildrenById(@PathVariable int id) {
        if (id <= 0) {
            return new ResponseEntity<>(new ErrorResponse(String.format("Task id must be a positive integer", id)),
                    HttpStatus.BAD_REQUEST);
        }
        Task task = repository.findById(id).orElse(null);
        if (task != null) {
            TaskModel model = new TaskModel(task);
            TaskArray children = new TaskArray(model.getChildren());
            return new ResponseEntity<>(children, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse(String.format("Task with id %d not found", id)),
                HttpStatus.NOT_FOUND);
    }
}
