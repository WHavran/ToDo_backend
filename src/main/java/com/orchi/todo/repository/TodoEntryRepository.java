package com.orchi.todo.repository;

import com.orchi.todo.model.entity.TodoEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TodoEntryRepository extends JpaRepository<TodoEntry, Long>, JpaSpecificationExecutor<TodoEntry> {
    // previous solution queries
//    @Query("""
//            SELECT new com.orchi.todo.model.dto.TodoEntryListDTO
//            (t.id, t.title, t.status, t.deadline)
//            FROM TodoEntry t
//            """)
//    Page<TodoEntryListDTO> findAllByListDTO(Pageable pageable);
//
//    @Query("""
//            SELECT new com.orchi.todo.model.dto.TodoEntryListDTO
//            (t.id, t.title, t.status, t.deadline)
//            FROM TodoEntry t
//            WHERE t.status = :inStatus
//            """)
//    Page<TodoEntryListDTO> findListDTOByStatus(Pageable pageable,@Param("inStatus") Status inStatus);
//
//    @Query("""
//            SELECT new com.orchi.todo.model.dto.TodoEntryListDTO
//            (t.id, t.title, t.status, t.deadline)
//            FROM TodoEntry t
//            WHERE t.deadline = :inDeadline
//            """)
//    Page<TodoEntryListDTO> findListDTOByDeadline(Pageable pageable,@Param("inDeadline") LocalDate inDeadline);
//
//    @Query("""
//            SELECT new com.orchi.todo.model.dto.TodoEntryListDTO
//            (t.id, t.title, t.status, t.deadline)
//            FROM TodoEntry t
//            WHERE t.deadline = :inDeadline
//            AND (t.status = 'CREATED' OR t.status = 'IN_PROCESS')
//            """)
//    Page<TodoEntryListDTO> findListDTOActiveByDeadline(Pageable pageable,@Param("inDeadline") LocalDate inDeadline);
//
//    @Query("""
//            SELECT new com.orchi.todo.model.dto.TodoEntryListDTO
//            (t.id, t.title, t.status, t.deadline)
//            FROM TodoEntry t
//            WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :input, '%'))
//            """)
//    Page<TodoEntryListDTO> findListDTOByTitle(Pageable pageable,@Param("input") String input);


}
