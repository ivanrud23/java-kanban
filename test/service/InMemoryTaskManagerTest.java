package service;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @BeforeEach
    void createManager() {
        taskManager = new InMemoryTaskManager();
    }


}