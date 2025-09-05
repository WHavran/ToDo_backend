package com.orchi.todo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.orchi.todo.mapper.TodoEntryMapper;
import com.orchi.todo.model.dto.TodoEntryCreateDTO;
import com.orchi.todo.model.dto.TodoEntryDetailDTO;
import com.orchi.todo.model.dto.TodoEntryListDTO;
import com.orchi.todo.model.entity.TodoEntry;
import com.orchi.todo.model.enums.Status;
import com.orchi.todo.repository.TodoEntryRepository;
import com.orchi.todo.specification.TodoEntrySpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Service
public class TodoEntryServiceImpl implements TodoEntryService{

    private final TodoEntryRepository todoEntryRepository;
    private final TodoEntryMapper todoEntryMapper;
    private final ObjectMapper objectMapper;
    private final Validator validator;


    @Autowired
    public TodoEntryServiceImpl(TodoEntryRepository todoEntryRepository, TodoEntryMapper todoEntryMapper, ObjectMapper objectMapper, Validator validator) {
        this.todoEntryRepository = todoEntryRepository;
        this.todoEntryMapper = todoEntryMapper;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @Override
    public TodoEntry getEntityById(long theId) {
        return todoEntryRepository.findById(theId)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public TodoEntryDetailDTO getDetailDTOById(long theId) {
        TodoEntry entity = getEntityById(theId);
        return todoEntryMapper.mapEntityToDetailDTO(entity);
    }
    @Override
    public Page<TodoEntryListDTO> getAll(String title, Status status, LocalDate deadLine, Pageable pageable) {

        Specification<TodoEntry> spec = TodoEntrySpecifications.hasTitle(title)
                .and(status != null ? TodoEntrySpecifications.hasStatus(status) : null)
                .and(deadLine != null ? TodoEntrySpecifications.hasDeadline(deadLine) : null);

        return todoEntryRepository.findAll(spec, pageable)
                .map(todoEntryMapper::mapEntityToListDTO);
    }

    @Override
    public TodoEntryDetailDTO createNew(TodoEntryCreateDTO createDTO) {
        TodoEntry newEntity = todoEntryMapper.mapCreateDTOToEntity(createDTO);
        TodoEntry savedEntity = todoEntryRepository.save(newEntity);
        return todoEntryMapper.mapEntityToDetailDTO(savedEntity);
    }

    @Override
    public TodoEntryDetailDTO update(TodoEntryDetailDTO detailDTO) {
        TodoEntry dbEntity = getEntityById(detailDTO.id());
        todoEntryMapper.mapDetailDTOToEntityForUpdate(dbEntity, detailDTO);
        todoEntryRepository.save(dbEntity);
        return todoEntryMapper.mapEntityToDetailDTO(dbEntity);
    }

    @Override
    public TodoEntryDetailDTO patchUpdateById(Map<String, Object> patchInput, long theId) {
        if (patchInput.containsKey("id")){
            throw new IllegalArgumentException("Id is not allowed in request body ");
        }
        TodoEntry entity = getEntityById(theId);
        entity = patchMerge(patchInput, entity);
        TodoEntryDetailDTO dtoForValidation = todoEntryMapper.mapEntityToDetailDTO(entity);
        Set<ConstraintViolation<TodoEntryDetailDTO>> violations = validator.validate(dtoForValidation);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        TodoEntry savedEntity = todoEntryRepository.save(entity);
        return todoEntryMapper.mapEntityToDetailDTO(savedEntity);
    }

    @Override
    public void deleteById(long theId) {
        if (!todoEntryRepository.existsById(theId)){
            throw new EntityNotFoundException();
        }
        todoEntryRepository.deleteById(theId);
    }

    private TodoEntry patchMerge(Map<String, Object> patchInput, TodoEntry entity) {
        if ((patchInput.containsKey("status") &&
                (patchInput.get("status") == Status.FINISHED ||
                        (patchInput.get("status") instanceof String s && s.equalsIgnoreCase(Status.FINISHED.name()))
                )
        ) && !patchInput.containsKey("completedAt")) {
            patchInput.put("completedAt", LocalDate.now());
        }

        if (patchInput.containsKey("status")) {
            Object newStatusRaw = patchInput.get("status");
            String newStatusStr = null;

            if (newStatusRaw instanceof Status statusEnum) {
                newStatusStr = statusEnum.name();
            } else if (newStatusRaw instanceof String s) {
                newStatusStr = s.toUpperCase();
            }

            if (newStatusStr != null) {
                if (entity.getStatus() == Status.FINISHED &&
                        (newStatusStr.equals(Status.IN_PROCESS.name()) || newStatusStr.equals(Status.CREATED.name()))) {
                    patchInput.put("completedAt", null);
                }
                patchInput.put("status", newStatusStr);
            }
        }
        try {
            ObjectReader mergeProvider = objectMapper.readerForUpdating(entity);
            return mergeProvider.readValue(objectMapper.writeValueAsString(patchInput));
        } catch (JsonProcessingException e) {
            throw new HttpMessageNotReadableException("Failed to parse JSON", e, null);
        }
    }

}
