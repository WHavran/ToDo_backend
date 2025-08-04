package com.orchi.todo.model.dto;

import com.orchi.todo.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TodoEntryDetailDTO(
        long id,
        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 50, message = "Required range 2 - 50 characters")
        String title,
        @NotBlank(message = "Description is required")
        @Size(min = 5, max = 500, message = "Required range 5 - 500 characters")
        String description,
        @NotNull(message = "Required")
        Status status,
        @NotNull(message = "Deadline is required")
        LocalDate createdAt,
        @NotNull(message = "Deadline is required")
        LocalDate deadline,
        LocalDate completedAt
) {
}
