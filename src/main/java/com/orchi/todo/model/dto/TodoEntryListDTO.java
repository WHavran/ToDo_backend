package com.orchi.todo.model.dto;

import com.orchi.todo.model.enums.Status;

import java.time.LocalDate;

public record TodoEntryListDTO(
        long id,
        String title,
        Status status,
        LocalDate deadline
) {
}
