package com.github.fightkittens.kronos.controller;

import com.github.fightkittens.kronos.entities.Task;
import com.github.fightkittens.kronos.model.*;
import com.github.fightkittens.kronos.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

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
                    if (childTask.getParent() != null) {
                        return new ResponseEntity<>(new ErrorResponse(String.format("Child with id %d already has a parent", id)), HttpStatus.BAD_REQUEST);
                    }
                    childTask.setParent(task);
                    task.addChild(childTask);
                    repository.save(task);
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
                    if (childTask.getParent() != null) {
                        return new ResponseEntity<>(new ErrorResponse(String.format("Child with id %d already has a parent", id)), HttpStatus.BAD_REQUEST);
                    }
                    childTask.setParent(task);
                    task.addChild(childTask);
                    repository.save(task);
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
            SortedSet<TaskModel> results = repository.findAllById(model.getConnected())
                    .stream().map(TaskModel::new).collect(Collectors.toCollection(TreeSet::new));
            TaskArray connected = new TaskArray(results);
            return new ResponseEntity<>(connected, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse(String.format("Task with id %d not found", id)),
                HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/connected/batch", produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> getConnectedById(@RequestBody TaskIdArray ids) {
        Set<Integer> resultIds = new TreeSet<>();
        Set<Integer> tasks = ids.getTasks();
        for (Integer id : tasks) {
            if (id <= 0) {
                return new ResponseEntity<>(new ErrorResponse(String.format("Task id must be a positive integer", id)),
                        HttpStatus.BAD_REQUEST);
            }
            Task task = repository.findById(id).orElse(null);
            if (task != null) {
                TaskModel model = new TaskModel(task);
                resultIds.addAll(model.getConnected());
            } else {
                return new ResponseEntity<>(new ErrorResponse(String.format("Task with id %d not found", id)),
                        HttpStatus.NOT_FOUND);
            }
        }

        SortedSet<TaskModel> results = repository.findAllById(resultIds).stream()
                .map(TaskModel::new).collect(Collectors.toCollection(TreeSet::new));
        return new ResponseEntity<>(new TaskArray(results), HttpStatus.OK);
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
            SortedSet<TaskModel> results = repository.findAllById(model.getChildren())
                    .stream().map(TaskModel::new).distinct().collect(Collectors.toCollection(TreeSet::new));
            TaskArray children = new TaskArray(results);
            return new ResponseEntity<>(children, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse(String.format("Task with id %d not found", id)),
                HttpStatus.NOT_FOUND);
    }


    @GetMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> filterByDate(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        try {
            Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
            if (end.before(start)) {
                return new ResponseEntity<>(new ErrorResponse("The end date is before the start date"),
                        HttpStatus.BAD_REQUEST);
            }
            int duration = (int) DAYS.between(start.toInstant(), end.toInstant());
            SortedSet<TaskModel> results = repository.filterByDate(start, duration)
                    .stream().filter(t -> (t.getDuration() <= DAYS.between(t.getStartDate().toInstant(), end.toInstant())))
                    .map(TaskModel::new).collect(Collectors.toCollection(TreeSet::new));
            return new ResponseEntity<>(new TaskArray(results), HttpStatus.OK);
        } catch (ParseException e) {
            return new ResponseEntity<>(new ErrorResponse("Invalid date format"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/schedule/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> filterBySchedule(@PathVariable("id") int id) {
        try {
            SortedSet<Integer> results = repository.filterBySchedule(id)
                    .stream().map(TaskModel::new).map(TaskModel::getId).collect(Collectors.toCollection(TreeSet::new));
            return new ResponseEntity<>(new TaskIdArray(results), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Invalid date format"),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
