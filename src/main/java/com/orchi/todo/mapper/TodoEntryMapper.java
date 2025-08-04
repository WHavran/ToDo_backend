package com.orchi.todo.mapper;

import com.orchi.todo.model.dto.TodoEntryCreateDTO;
import com.orchi.todo.model.dto.TodoEntryDetailDTO;
import com.orchi.todo.model.dto.TodoEntryListDTO;
import com.orchi.todo.model.entity.TodoEntry;

public interface TodoEntryMapper {

    TodoEntry mapCreateDTOToEntity(TodoEntryCreateDTO todoDTO);

    void mapDetailDTOToEntityForUpdate(TodoEntry entity, TodoEntryDetailDTO todoDTO);

    TodoEntryListDTO mapEntityToListDTO(TodoEntry entity);

    TodoEntryDetailDTO mapEntityToDetailDTO(TodoEntry entity);
}
