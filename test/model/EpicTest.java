package model;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    @AfterEach
    void createInMemoryTaskManager() throws IOException, InterruptedException {
        inMemoryTaskManager.clearTask();
        inMemoryTaskManager.clearEpic();
        inMemoryTaskManager.clearSubtask();
    }


    @Test
    void EpicStatusNewWithEmptySubtask() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        assertEquals(Status.NEW, inMemoryTaskManager.getById(1).getStatus());
    }

    @Test
    void EpicStatusNewWithSubtaskStatusNew() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        inMemoryTaskManager.createEpic(new Epic("Epic_2", "Desk_Epic_2"));
        inMemoryTaskManager.createEpic(new Epic("Epic_3", "Desk_Epic_3"));
        assertEquals(Status.NEW, inMemoryTaskManager.getById(1).getStatus());
    }

    @Test
    void EpicStatusDoneWithSubtaskStatusDone() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        inMemoryTaskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", 1));
        inMemoryTaskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", 1));
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
    void EpicStatusInProgressWithSubtaskStatusNewAndDone() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        inMemoryTaskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", 1));
        inMemoryTaskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", 1));
        inMemoryTaskManager.updateSubTask(
                2,
                new Subtask("Sub_1", "Desk_Sub_1", 2, Status.DONE, 1)
        );
        assertEquals(Status.IN_PROGRESS, inMemoryTaskManager.getById(1).getStatus());
    }

    @Test
    void EpicStatusInProgressWithSubtaskStatusInProgress() throws IOException, InterruptedException {
        inMemoryTaskManager.createEpic(new Epic("Epic_1", "Desk_Epic_1"));
        inMemoryTaskManager.createSubTask(new Subtask("Sub_1", "Desk_Sub_1", 1));
        inMemoryTaskManager.createSubTask(new Subtask("Sub_2", "Desk_Sub_2", 1));
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