package com.github.fightkittens.kronos.controller;

import com.github.fightkittens.kronos.entities.Task;
import com.github.fightkittens.kronos.model.ErrorResponse;
import com.github.fightkittens.kronos.model.TaskModel;
import com.github.fightkittens.kronos.model.TaskResponse;
import com.github.fightkittens.kronos.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    TaskRepository repository;

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> getById(@PathVariable int id) {
        Task task = repository.findById(id).orElse(null);
        if (task != null) {
            TaskModel model = new TaskModel(task);
            return new ResponseEntity<>(model, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse(String.format("Task with id %d not found", id)),
                HttpStatus.BAD_REQUEST);
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
}
