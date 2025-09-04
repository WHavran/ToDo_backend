package com.orchi.todo.specification;

import com.orchi.todo.model.entity.TodoEntry;
import com.orchi.todo.model.enums.Status;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TodoEntrySpecifications {

    public static Specification<TodoEntry> hasTitle(String title){
        return ((root, query, cb) ->
                (title == null) ? null : cb.like(cb.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%"));
    }

    public static Specification<TodoEntry> hasStatus(Status status){
        return ((root, query, cb) ->
                (status == null) ? null : cb.equal(root.get("status"), status));
    }

    public static Specification<TodoEntry> hasDeadline(LocalDate deadline){
        return (((root, query, cb) ->
                (deadline == null) ? null : cb.equal(root.get("deadline"), deadline)));
    }
}
