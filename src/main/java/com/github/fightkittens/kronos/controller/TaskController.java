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
            return new ResponseEntity<TaskModel>(model, HttpStatus.OK);
        }
        return new ResponseEntity<ErrorResponse>(new ErrorResponse(String.format("Task with id %d not found", id)),
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
            repository.saveAndFlush(task);
            return new ResponseEntity<TaskModel>(new TaskModel(task), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("Exception: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
