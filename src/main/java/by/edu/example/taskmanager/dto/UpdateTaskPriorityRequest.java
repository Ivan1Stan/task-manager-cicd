package by.edu.example.taskmanager.dto;

import by.edu.example.taskmanager.model.TaskPriority;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskPriorityRequest(
        @NotNull(message = "Приоритет обязателен")
        TaskPriority priority
) {
}