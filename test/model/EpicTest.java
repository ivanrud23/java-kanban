package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    @AfterEach
    void createInMemoryTaskManager() throws IOException {
        inMemoryTaskManager.clearTask();
        inMemoryTaskManager.clearEpic();
        inMemoryTaskManager.clearSubtask();
    }


    @Test
    void EpicStatusNewWithEmptySubtask() throws IOException {
        inMemoryTaskManager.createEpic("Epic_1", "Desk_Epic_1");
        assertEquals(Status.NEW, inMemoryTaskManager.getById(1).getStatus());
    }

    @Test
    void EpicStatusNewWithSubtaskStatusNew() throws IOException {
        inMemoryTaskManager.createEpic("Epic_1", "Desk_Epic_1");
        inMemoryTaskManager.createSubTask("Sub_1", "Desk_Sub_1", 1);
        inMemoryTaskManager.createSubTask("Sub_2", "Desk_Sub_2", 1);
        assertEquals(Status.NEW, inMemoryTaskManager.getById(1).getStatus());
    }

    @Test
    void EpicStatusDoneWithSubtaskStatusDone() throws IOException {
        inMemoryTaskManager.createEpic("Epic_1", "Desk_Epic_1");
        inMemoryTaskManager.createSubTask("Sub_1", "Desk_Sub_1", 1);
        inMemoryTaskManager.createSubTask("Sub_2", "Desk_Sub_2", 1);
        inMemoryTaskManager.updateSubTask(
                2,
                new Subtask("Sub_1", "Desk_Sub_1", 2, Status.DONE, 1)
                );
        inMemoryTaskManager.updateSubTask(
                3,
                new Subtask("Sub_2", "Desk_Sub_2", 3, Status.DONE, 1)
        );
        assertEquals(Status.DONE, inMemoryTaskManager.getById(1).getStatus());
    }

    @Test
    void EpicStatusInProgressWithSubtaskStatusNewAndDone() throws IOException {
        inMemoryTaskManager.createEpic("Epic_1", "Desk_Epic_1");
        inMemoryTaskManager.createSubTask("Sub_1", "Desk_Sub_1", 1);
        inMemoryTaskManager.createSubTask("Sub_2", "Desk_Sub_2", 1);
        inMemoryTaskManager.updateSubTask(
                2,
                new Subtask("Sub_1", "Desk_Sub_1", 2, Status.DONE, 1)
        );
        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getById(1).getStatus());
    }

    @Test
    void EpicStatusInProgressWithSubtaskStatusInProgress() throws IOException {
        inMemoryTaskManager.createEpic("Epic_1", "Desk_Epic_1");
        inMemoryTaskManager.createSubTask("Sub_1", "Desk_Sub_1", 1);
        inMemoryTaskManager.createSubTask("Sub_2", "Desk_Sub_2", 1);
        inMemoryTaskManager.updateSubTask(
                2,
                new Subtask("Sub_1", "Desk_Sub_1", 2, Status.IN_PROGRESS, 1)
        );
        inMemoryTaskManager.updateSubTask(
                3,
                new Subtask("Sub_2", "Desk_Sub_2", 3, Status.IN_PROGRESS, 1)
        );
        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getById(1).getStatus());
    }
}