package com.orchi.todo.units;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orchi.todo.mapper.TodoEntryMapper;
import com.orchi.todo.model.dto.TodoEntryDetailDTO;
import com.orchi.todo.model.entity.TodoEntry;
import com.orchi.todo.model.enums.Status;
import com.orchi.todo.repository.TodoEntryRepository;
import com.orchi.todo.service.TodoEntryServiceImpl;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnitsExampleTests {

    @Mock
    private TodoEntryRepository todoEntryRepository;

    @Mock
    private TodoEntryMapper todoEntryMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private TodoEntryServiceImpl service;

    @BeforeEach
    void setup() {
        service = new TodoEntryServiceImpl(todoEntryRepository, todoEntryMapper, new ObjectMapper(), validator);
    }

    @Test
    void patchUpdateById_ThrowsEx() {
        Map<String, Object> patchInput = new HashMap<>();
        patchInput.put("id", 123L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.patchUpdateById(patchInput, 1L));

        assertEquals("Id is not allowed in request body ", ex.getMessage());
    }

    @Test
    void patchUpdateById_Success() {
        Map<String, Object> patchInput = new HashMap<>();
        patchInput.put("title", "Old title");
        long id = 1L;

        TodoEntry existingEntity = new TodoEntry();
        existingEntity.setId(id);

        TodoEntry patchedEntity = new TodoEntry();
        patchedEntity.setId(id);
        patchedEntity.setTitle("Tittle test");

        TodoEntryDetailDTO dtoAfterPatch = new TodoEntryDetailDTO(
                id, "Tittle test", "Some description", Status.CREATED,
                LocalDate.now(), LocalDate.now().plusDays(1), null
        );

        when(todoEntryRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(todoEntryMapper.mapEntityToDetailDTO(any(TodoEntry.class))).thenReturn(dtoAfterPatch);
        when(validator.validate(any(TodoEntryDetailDTO.class))).thenReturn(Collections.emptySet());
        when(todoEntryRepository.save(any(TodoEntry.class))).thenReturn(patchedEntity);

        TodoEntryDetailDTO result = service.patchUpdateById(patchInput, id);

        assertNotNull(result);
        assertEquals("Tittle test", result.title());
    }


}
