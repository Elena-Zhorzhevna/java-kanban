package service.managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.InMemory.InMemoryHistoryManager;
import service.InMemory.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {

    @Test
    public void assertEqualsInMemoryHistoryManagerTest() {
        InMemoryHistoryManager expected = new InMemoryHistoryManager();
        InMemoryHistoryManager actual = Managers.getDefaultHistory();
        Assertions.assertNotNull(actual, "Объект не был создан.");
        assertEquals(expected.getHistory(), actual.getHistory(), ", history");
    }

    @Test
    public void assertEqualsTaskManagerTest() {
        TaskManager expected = new InMemoryTaskManager();
        TaskManager actual = Managers.getDefault();
        Assertions.assertNotNull(actual, "Объект не был создан.");
        assertEquals(expected.getAllTasks(), actual.getAllTasks(), ", tasks");
    }
}