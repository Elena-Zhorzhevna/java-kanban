package service.managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.InMemory.InMemoryHistoryManager;
import service.InMemory.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {

    @Test //проверяем, возвращает ли класс Manager проинициализированные и готовые к работе экземпляры менеджеров
    public void assertEqualsInMemoryHistoryManagerTest() {
        HistoryManager expected = new InMemoryHistoryManager();
        HistoryManager actual = Managers.getDefaultHistory();
        Assertions.assertNotNull(actual, "Объект не был создан.");
        assertEquals(expected.getHistory(), actual.getHistory(), ", history");
    }

    @Test //проверяем, возвращает ли класс Manager проинициализированные и готовые к работе экземпляры менеджеров
    public void assertEqualsTaskManagerTest() {
        TaskManager expected = new InMemoryTaskManager();
        TaskManager actual = Managers.getDefault();
        Assertions.assertNotNull(actual, "Объект не был создан.");
        assertEquals(expected.getAllTasks(), actual.getAllTasks(), ", tasks");
    }
}