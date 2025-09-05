package com.orchi.todo.service;

import com.orchi.todo.model.dto.TodoEntryCreateDTO;
import com.orchi.todo.model.dto.TodoEntryDetailDTO;
import com.orchi.todo.model.dto.TodoEntryListDTO;
import com.orchi.todo.model.entity.TodoEntry;
import com.orchi.todo.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;

public interface TodoEntryService {

    TodoEntry getEntityById(long theId);

    TodoEntryDetailDTO getDetailDTOById(long theId);

    Page<TodoEntryListDTO> getAll(String title, Status status, LocalDate deadLine, Pageable pageable);

    TodoEntryDetailDTO createNew(TodoEntryCreateDTO createDTO);

    TodoEntryDetailDTO update(TodoEntryDetailDTO detailDTO);

    TodoEntryDetailDTO patchUpdateById(Map<String, Object> patchInput, long theId);

    void deleteById(long theId);


}
