package com.github.fightkittens.kronos.controller;

import com.github.fightkittens.kronos.entities.Task;
import com.github.fightkittens.kronos.model.*;
import com.github.fightkittens.kronos.repositories.TaskRepository;
import com.github.fightkittens.kronos.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    TaskRepository repository;
    @Autowired
    TaskService service;

    private static Logger logger = LoggerFactory.getLogger(TaskController.class);

    @GetMapping(value = "/all", produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> getAll() {
        SortedSet<TaskModel> tasks = repository.findAll().stream().map(TaskModel::new).collect(Collectors.toCollection(TreeSet::new));
        return new ResponseEntity<>(new TaskArray(tasks), HttpStatus.OK);
    }

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

            for (Integer childId : model.getChildren()) {
                Task childTask = repository.findById(childId).orElse(null);
                if (childTask == null) {
                    return new ResponseEntity<>(new ErrorResponse(String.format("Child with id %d not found", childId)), HttpStatus.BAD_REQUEST);
                } else {
                    task.addChild(childTask);
                    repository.save(task);
                    repository.save(childTask);
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
            Task task = new Task(model);
            repository.save(task);
            for (Integer id : model.getChildren()) {
                Task childTask = repository.findById(id).orElse(null);
                if (childTask == null) {
                    return new ResponseEntity<>(new ErrorResponse(String.format("Child with id %d not found", id)), HttpStatus.BAD_REQUEST);
                } else {
                    task.addChild(childTask);
                    repository.save(task);
                    repository.save(childTask);
                }
            }
            repository.saveAndFlush(task);
            return new ResponseEntity<>(new TaskModel(task), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("Exception: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/batch", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> addBatch(@RequestBody TaskArray array) {
        try {
            SortedSet<Integer> tasks = new TreeSet<>();
            SortedSet<TaskModel> modelArray = array.getTasks();
            for (TaskModel model : modelArray) {
                Task task = new Task(model);
                repository.save(task);
                for (Integer id : model.getChildren()) {
                    Task childTask = repository.findById(id).orElse(null);
                    if (childTask == null) {
                        return new ResponseEntity<>(new ErrorResponse(String.format("Child with id %d not found", id)), HttpStatus.BAD_REQUEST);
                    } else {
                        task.addChild(childTask);
                        repository.save(task);
                        repository.save(childTask);
                        logger.info("{}: {}", task.getId(), childTask.getId());
                    }
                }
                tasks.add(task.getId());
                repository.saveAndFlush(task);
            }
            return new ResponseEntity<>(new TaskIdArray(tasks), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("Exception: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
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
                    .stream().map(TaskModel::new).collect(Collectors.toCollection(TreeSet::new));
            TaskArray children = new TaskArray(results);
            return new ResponseEntity<>(children, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse(String.format("Task with id %d not found", id)),
                HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/parents/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> getParents(@PathVariable int id) {
        if (id <= 0) {
            return new ResponseEntity<>(new ErrorResponse(String.format("Task id must be a positive integer", id)),
                    HttpStatus.BAD_REQUEST);
        }
        Task task = repository.findById(id).orElse(null);
        if (task != null) {
            SortedSet<TaskModel> results = repository.findAll()
                    .stream().filter(t -> t.getChildren().stream().anyMatch(tsk -> tsk.getId() == id)).map(TaskModel::new).collect(Collectors.toCollection(TreeSet::new));
            TaskArray parents = new TaskArray(results);
            return new ResponseEntity<>(parents, HttpStatus.OK);
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
            SortedSet<TaskModel> results = repository.filterByDate(start, end)
                    .stream().map(TaskModel::new).collect(Collectors.toCollection(TreeSet::new));
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

    @GetMapping(value = "/schedule", produces = "application/json")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> getSchedules() {
        try {
            SortedSet<Integer> results = new TreeSet<>(repository.getSchedules());
            return new ResponseEntity<>(new TaskIdArray(results), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Invalid request"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/plan/calculate")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> calculateChanges(@RequestBody TaskMoves moves) {
        try {
            long result = service.calculate(moves.getMoves());
            return new ResponseEntity<>(new PenaltyValue(result), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("Exception: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/plan/append")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> appendChanges(@RequestBody TaskMoves moves) {
        try {
            service.append(moves.getMoves());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("Exception: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/plan/current")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> getCurrentPrice() {
        try {
            return new ResponseEntity<>(new PenaltyValue(service.getCurrentPrice()), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("Exception: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/plan/current")
    @ResponseBody
    public ResponseEntity<? extends TaskResponse> setCurrentPrice(@RequestParam("price") long price) {
        try {
            service.setCurrentPrice(price);
            return new ResponseEntity<>(new PenaltyValue(service.getCurrentPrice()), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("Exception: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}