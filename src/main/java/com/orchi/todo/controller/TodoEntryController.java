package com.orchi.todo.controller;

import com.orchi.todo.model.dto.TodoEntryCreateDTO;
import com.orchi.todo.model.dto.TodoEntryDetailDTO;
import com.orchi.todo.model.dto.TodoEntryListDTO;
import com.orchi.todo.service.TodoEntryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("api/todoEntry")
public class TodoEntryController {

    private TodoEntryService todoEntryService;

    @Autowired
    public TodoEntryController(TodoEntryService todoEntryService) {
        this.todoEntryService = todoEntryService;
    }

    @GetMapping("/{id}")
    public TodoEntryDetailDTO getOneById(@PathVariable long id){
        return todoEntryService.getDetailDTOById(id);
    }

    @GetMapping()
    public Page<TodoEntryListDTO> getAllTodos(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate deadline,
            Pageable pageable){
        return todoEntryService.getAll(title,status,deadline,pageable);
    }

    @PostMapping()
    public ResponseEntity<TodoEntryDetailDTO> createNew(@Valid @RequestBody TodoEntryCreateDTO inputDTO){
        TodoEntryDetailDTO savedDTO = todoEntryService.createNew(inputDTO);
        URI newUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDTO.id())
                .toUri();
        return ResponseEntity.created(newUri).body(savedDTO);
    }

    @PutMapping()
    public TodoEntryDetailDTO updatePut(@Valid @RequestBody TodoEntryDetailDTO inputDTO){
        return todoEntryService.update(inputDTO);
    }

    @PatchMapping("/{id}")
    public TodoEntryDetailDTO updatePatch(@PathVariable long id,
                                          @RequestBody Map<String, Object> patchInput){
        return todoEntryService.patchUpdateById(patchInput, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        todoEntryService.deleteById(id);
    }

}
