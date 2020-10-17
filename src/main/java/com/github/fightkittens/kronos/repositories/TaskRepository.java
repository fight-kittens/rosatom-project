package com.github.fightkittens.kronos.repositories;

import com.github.fightkittens.kronos.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Collection;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query(value = "FROM Task t WHERE t.startDate >= :start AND t.startDate <= :end")
    Collection<Task> filterByDate(@Param("start") Date start, @Param("end") Date end);
    @Query(value = "FROM Task t WHERE t.scheduleId = :id")
    Collection<Task> filterBySchedule(@Param("id") int id);
}
