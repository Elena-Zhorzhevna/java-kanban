package service.managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.in_memory.InMemoryHistoryManager;
import service.in_memory.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static service.managers.Managers.getDefaultHistory;

class ManagersTest {

    @Test //проверяем, возвращает ли класс Manager проинициализированные и готовые к работе экземпляры менеджеров
    public void assertEqualsInMemoryHistoryManagerTest() {
        HistoryManager expected = new InMemoryHistoryManager();
        HistoryManager actual = getDefaultHistory();
        Assertions.assertNotNull(actual, "Объект не был создан.");
        assertEquals(expected.getHistory(), actual.getHistory(), ", history");
    }

    @Test //проверяем, возвращает ли класс Manager проинициализированные и готовые к работе экземпляры менеджеров
    public void assertEqualsTaskManagerTest() {
        TaskManager expected = new InMemoryTaskManager(getDefaultHistory());
        TaskManager actual = Managers.getDefault();
        Assertions.assertNotNull(actual, "Объект не был создан.");
        assertEquals(expected.getAllTasks(), actual.getAllTasks(), ", tasks");
    }
}