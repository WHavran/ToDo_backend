package com.orchi.todo.model.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record TodoEntryCreateDTO(
        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 50, message = "Required range 2 - 50 characters")
        String title,
        @NotBlank(message = "Description is required")
        @Size(min = 5, max = 500, message = "Required range 5 - 500 characters")
        String description,
        @NotNull(message = "Deadline is required")
        @FutureOrPresent(message = "The deadline has to be in the future or present")
        LocalDate deadline
) {
}
