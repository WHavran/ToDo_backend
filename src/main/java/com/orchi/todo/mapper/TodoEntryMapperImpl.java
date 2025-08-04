package com.orchi.todo.mapper;

import com.orchi.todo.model.dto.TodoEntryCreateDTO;
import com.orchi.todo.model.dto.TodoEntryDetailDTO;
import com.orchi.todo.model.dto.TodoEntryListDTO;
import com.orchi.todo.model.entity.TodoEntry;
import com.orchi.todo.model.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TodoEntryMapperImpl implements TodoEntryMapper{

    @Override
    public TodoEntry mapCreateDTOToEntity(TodoEntryCreateDTO todoDTO) {
        TodoEntry entity = new TodoEntry();
        entity.setTitle(todoDTO.title());
        entity.setDescription(todoDTO.description());
        entity.setStatus(Status.CREATED);
        entity.setCreatedAt(LocalDate.now());
        entity.setDeadline(todoDTO.deadline());

        return entity;
    }

    @Override
    public void mapDetailDTOToEntityForUpdate(TodoEntry entity, TodoEntryDetailDTO todoDTO) {
        entity.setTitle(todoDTO.title());
        entity.setDescription(todoDTO.description());
        entity.setCreatedAt(todoDTO.createdAt());
        entity.setDeadline(todoDTO.deadline());
        entity.setCompletedAt(completedChangeLeader(todoDTO, entity));
        entity.setStatus(todoDTO.status());

    }

    @Override
    public TodoEntryListDTO mapEntityToListDTO(TodoEntry entity) {
        return new TodoEntryListDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getStatus(),
                entity.getDeadline()
        );
    }


    @Override
    public TodoEntryDetailDTO mapEntityToDetailDTO(TodoEntry entity) {
        return new TodoEntryDetailDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getDeadline(),
                entity.getCompletedAt()
        );
    }

    public LocalDate completedChangeLeader(TodoEntryDetailDTO todoDTO, TodoEntry entity) {
        if (todoDTO.status() == Status.FINISHED && todoDTO.completedAt() == null) {
            System.out.println("Setting completedAt to now");
            return LocalDate.now();
        }

        if ((todoDTO.status() == Status.IN_PROCESS || todoDTO.status() == Status.CREATED)
                && entity.getStatus() == Status.FINISHED) {
            return null;
        }
        return todoDTO.completedAt();
    }
}
