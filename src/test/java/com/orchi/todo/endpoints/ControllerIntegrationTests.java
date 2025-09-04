package com.orchi.todo.endpoints;

import com.orchi.todo.repository.TodoEntryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@TestPropertySource("classpath:testing.properties")
public class ControllerIntegrationTests {

    @Autowired
    private TodoEntryRepository todoEntryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {

        String sql = """
                INSERT INTO todoentry (title, description, status, created_at, deadline, completed_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql, "First Task", "Initial setup and configuration", "CREATED",
                LocalDate.of(2025, 8, 1),
                LocalDate.of(2025, 8, 10),
                null);

        jdbcTemplate.update(sql, "Second Task", "Implement basic functionality", "IN_PROCESS",
                LocalDate.of(2025, 8, 2),
                LocalDate.of(2025, 8, 15),
                null);

        jdbcTemplate.update(sql, "Third Task", "Finalize feature and testing", "FINISHED",
                LocalDate.of(2025, 7, 20),
                LocalDate.of(2025, 7, 30),
                LocalDate.of(2025, 7, 29));

        jdbcTemplate.update(sql, "Fourth Task", "Experimental approach failed", "FAILED",
                LocalDate.of(2025, 7, 25),
                LocalDate.of(2025, 8, 5),
                LocalDate.of(2025, 7, 28));

        jdbcTemplate.update(sql, "Fifth Task", "Waiting for review", "IN_PROCESS",
                LocalDate.of(2025, 8, 3),
                LocalDate.of(2025, 8, 20),
                null);
        jdbcTemplate.update(sql, "Fifth2.0 Task", "Waiting for review", "IN_PROCESS",
                LocalDate.of(2025, 8, 3),
                LocalDate.of(2025, 9, 9),
                null);

    }

    @Test
    public void getOneHttpRequestCorrect() throws Exception {
        assertTrue(todoEntryRepository.findById(1L).isPresent());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/todoEntry/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("First Task"))
                .andExpect(jsonPath("$.description").value("Initial setup and configuration"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.deadline").value("2025-08-10"))
                .andExpect(jsonPath("$.completedAt").isEmpty());
    }

    @Test
    public void getOneHttpRequestFailed() throws Exception {
        assertFalse(todoEntryRepository.findById(999L).isPresent());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/todoEntry/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Entity not found")));
    }

    @Test
    public void getAllByParams()throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/api/todoEntry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.content.length()").value(6));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/todoEntry?title=F"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.content.length()").value(4));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/todoEntry?title=F&status=IN_PROCESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.content.length()").value(2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/todoEntry?title=F&status=IN_PROCESS&date=2025-09-09"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    public void postCreateHttpRequestCorrect() throws Exception {
        LocalDate future = LocalDate.now().plusDays(1);
        String jsonInput = String.format("""
                {
                "title": "Create Task",
                "description": "Added task by creating test",
                "deadline": "%s"
                }
                """, future.toString());

        mockMvc.perform(post("/api/todoEntry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Create Task"))
                .andExpect(jsonPath("$.description").value("Added task by creating test"))
                .andExpect(jsonPath("$.deadline").value(future.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.completedAt").isEmpty())
                .andExpect(jsonPath("$.createdAt").value(LocalDate.now().toString()));

        assertTrue(todoEntryRepository.findById(6L).isPresent());
    }

    @Test
    public void postCreateHttpRequestFailed() throws Exception {
        String jsonInput = """
                {
                "title": "Create Task",
                "description": "er",
                "deadline": "2025-01-01"
                }
                """;

        mockMvc.perform(post("/api/todoEntry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.listOfErrors[0].field", is("deadline")))
                .andExpect(jsonPath("$.listOfErrors[0].message", is("The deadline has to be in the future or present")))
                .andExpect(jsonPath("$.listOfErrors[1].field", is("description")))
                .andExpect(jsonPath("$.listOfErrors[1].message", is("Required range 5 - 500 characters")));

        assertFalse(todoEntryRepository.findById(7L).isPresent());
    }

    @Test
    public void putUpdateHttpRequestCorrect() throws Exception {
        String jsonInput = """
                {
                 "id": 4,
                 "title": "Fourth task Updated",
                 "description": "Completely updated description",
                 "status": "IN_PROCESS",
                 "createdAt": "2025-07-31",
                 "deadline": "2025-08-11",
                  "completedAt": null
                }
                """;

        mockMvc.perform(put("/api/todoEntry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Fourth task Updated"))
                .andExpect(jsonPath("$.description").value("Completely updated description"))
                .andExpect(jsonPath("$.deadline").value("2025-08-11"))
                .andExpect(jsonPath("$.status").value("IN_PROCESS"))
                .andExpect(jsonPath("$.completedAt").isEmpty())
                .andExpect(jsonPath("$.createdAt").value("2025-07-31"));

        assertTrue(todoEntryRepository.findById(4L).isPresent());
    }

    @Test
    public void putUpdateHttpRequestFailed() throws Exception {
        String jsonInputWrongId = """
                {
                 "id": 999,
                 "title": "Fourth task Updated",
                 "description": "Completely updated description",
                 "status": "IN_PROCESS",
                 "createdAt": "2025-07-31",
                 "deadline": "2025-08-11",
                  "completedAt": null
                }
                """;
        assertFalse(todoEntryRepository.findById(999L).isPresent());
        mockMvc.perform(put("/api/todoEntry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInputWrongId))
                .andExpect(status().isNotFound());

        String jsonInputBadStatus = """
                {
                        "id": 4,
                        "title": "Documentation",
                        "description": "Create swagger documentation 21",
                        "status": "Wrong",
                        "createdAt": "2025-07-31",
                        "deadline": "2025-08-01",
                        "completedAt": null
                }
                """;
        assertTrue(todoEntryRepository.findById(5L).isPresent());
        mockMvc.perform(put("/api/todoEntry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInputBadStatus))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid status. Allowed values: CREATED, IN_PROCESS, FINISHED, FAILED."));
    }

    @Test
    public void patchUpdateHttpRequestCorrect() throws Exception {
        String jsonPatch = """
                {
                        "title": "Second task Updated",
                        "description": "Lets updated second one",
                        "status": "FINISHED"
                }
                """;
        mockMvc.perform(patch("/api/todoEntry/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPatch))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Second task Updated"))
                .andExpect(jsonPath("$.description").value("Lets updated second one"))
                .andExpect(jsonPath("$.status").value("FINISHED"))
                .andExpect(jsonPath("$.createdAt").value("2025-08-02"))
                .andExpect(jsonPath("$.deadline").value("2025-08-15"))
                .andExpect(jsonPath("$.completedAt").value(LocalDate.now().toString()));
    }

    @Test
    public void patchUpdateHttpRequestFailed() throws Exception {
        String jsonWrongStatus = """
                {
                        "status": "Wrong"
                }
                """;

        assertFalse(todoEntryRepository.findById(999L).isPresent());
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/todoEntry/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWrongStatus))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Entity not found")));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/todoEntry/{id}", 4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWrongStatus))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid status. Allowed values: CREATED, IN_PROCESS, FINISHED, FAILED."));

    }

    @Test
    public void deleteHttpRequestCorrect() throws Exception {
        assertTrue(todoEntryRepository.findById(1L).isPresent());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/todoEntry/{id}", 1L))
                .andExpect(status().isOk());
        assertFalse(todoEntryRepository.findById(1L).isPresent());
    }

    @Test
    public void deleteHttpRequestFailed() throws Exception {
        assertFalse(todoEntryRepository.findById(999L).isPresent());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/todoEntry/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Entity not found")));
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE todoentry RESTART IDENTITY");
    }

}
