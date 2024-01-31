package com.dev13.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "active", nullable = false)
    private boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return isActive == task.isActive && Objects.equals(id, task.id) && Objects.equals(user, task.user) && Objects.equals(description, task.description) && Objects.equals(date, task.date) && Objects.equals(createDate, task.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, description, date, createDate, isActive);
    }
}
