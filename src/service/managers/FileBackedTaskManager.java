package service.managers;

import enums.Status;
import enums.TaskType;
import model.Epic;
import model.Subtask;
import model.Task;
import service.InMemory.InMemoryTaskManager;
import service.exception.ManagerSaveException;

import java.io.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public static void main(String[] args) throws IOException {
        //реализовано доп.задание 7 спринта
        /*FileBackedTaskManager bm = new FileBackedTaskManager(historyManager);
        Task task3 = new Task("t3", "dt3", Status.IN_PROGRESS);
        bm.createTask(task3);
        Epic epic1 = new Epic("e1", "de1", Status.DONE);
        bm.createEpic(epic1);
        Epic epic2 = new Epic("e2", "de2", Status.IN_PROGRESS);
        bm.createEpic(epic2);
        Task task1 = new Task("t1", "dt1", Status.NEW);
        bm.createTask(task1);
        Task task2 = new Task("t2", "dt2", Status.NEW);
        bm.createTask(task2);
        // bm.deleteAllTasks();
        System.out.println(bm.getAllTasks());
        System.out.println(bm.getAllEpics());
        System.out.println(bm.getAllSubtasks());
        Subtask subtask1 = new Subtask("s1", "ds1", Status.NEW, epic2.getId());
        bm.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("s2", "ds2", Status.IN_PROGRESS, epic1.getId());
        bm.createSubtask(subtask2);
        System.out.println(bm.getAllTasks());
        System.out.println(bm.getAllEpics());
        System.out.println(bm.getAllSubtasks());
        System.out.println("-----------");
        FileBackedTaskManager bm2 = new FileBackedTaskManager(historyManager);
        //bm2.deleteAllEpics();
        System.out.println(bm2.getAllTasks());
        System.out.println(bm.getAllEpics());
        System.out.println(bm.getAllSubtasks());
        System.out.println("-----------");
        bm.loadFromFile(file);
        System.out.println(bm.getAllTasks());
        System.out.println(bm.getAllEpics());
        bm.deleteAllEpics();
        bm.deleteAllTasks();
        bm.getAllSubtasks(); */
    }

    private static final String firstString = "id,type,name,status,description,epic"; //первая строка файла
    private static final File file = new File("TASK_CSV"); //файл для сохранения данных
    public static HistoryManager historyManager = Managers.getDefaultHistory();

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public FileBackedTaskManager() throws IOException {
        super(historyManager);
        loadFromFile(file);
    }

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
    }

    @Override
    public List<Task> getAllTasks() { //получение списка всех задач
        return super.getAllTasks();
    }

    @Override
    public List<Epic> getAllEpics() { //получение списка всех эпиков
        return super.getAllEpics();
    }

    @Override
    public List<Subtask> getAllSubtasks() { //получение списка всех подзадач
        return super.getAllSubtasks();
    }

    @Override
    public boolean deleteAllTasks() { //автосохранение удаления всех задач
        boolean result = super.deleteAllTasks();
        save();
        return result;
    }

    @Override
    public boolean deleteAllEpics() { //автосохранение удаления всех эпиков и подзадач
        boolean result = super.deleteAllEpics();
        save();
        return result;
    }

    @Override
    public boolean deleteAllSubtasks() { //автосохранение удаления всех подзадач
        boolean result = super.deleteAllSubtasks();
        save();
        return result;
    }

    @Override
    public Task createTask(Task task) { //автосохранение создания задачи
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) { //автосохранение создания эпика
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) { //автосохранение создания подзадачи
        Subtask newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public void updateTask(Task newTask) { //автосохранение обновления задачи
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) { //автосохранение обновления эпика
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) { //автосохранение обновления подзадачи
        super.updateSubtask(newSubtask);
        save();
    }

    @Override
    public void deleteByTaskId(int id) { //автосохранение удаления задачи по айди
        super.deleteByTaskId(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) { //автосохранение удаления эпика по айди
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) { //автосохранение удаления подзадачи по айди
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public List<Task> getHistory() { //возвращение списка просмотренных задач
        return super.getHistory();
    }

    public void save() { //сохранение текущего состояния менеджера в указанный файл
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(firstString + "\n");

            for (Task task : getAllTasks()) {
                bufferedWriter.write(toString(task));
            }

            for (Epic epic : getAllEpics()) {
                bufferedWriter.write(toString(epic));
            }

            for (Subtask subtask : getAllSubtasks()) {
                bufferedWriter.write(toString(subtask));
            }

        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }
    }

    public String toString(Task task) { //сохранение задачи в строку
        String result = switch (task.getType()) {
            case TASK -> task.getId() + ",TASK," + task.getTaskName() + ","
                    + task.getStatus() + "," + task.getDescription() + "\n";
            case EPIC -> task.getId() + ",EPIC," + task.getTaskName() + ","
                    + task.getStatus() + "," + task.getDescription() + "\n";
            case SUBTASK -> task.getId() + ",SUBTASK," + task.getTaskName() + ","
                    + task.getStatus() + "," + task.getDescription() + "," + ((Subtask) task).getEpicId() + "\n";
        };
        return result;
    }

    public static Task fromString(String value) { //создание задачи из строки
        if (value == null || value.isBlank()) {
            return null;
        }
        final String[] taskData = value.split(",");
        TaskType type = TaskType.valueOf(taskData[1]);
        switch (type) {
            case TASK:
                Task task = new Task();
                task.setTaskName(taskData[2].trim());
                task.setType(type);
                task.setDescription(taskData[4].trim());
                task.setStatus(Status.valueOf(taskData[3].trim()));
                task.setId(Integer.parseInt(taskData[0].trim()));
                return task;

            case EPIC:
                Epic epic = new Epic();
                epic.setId(Integer.parseInt(taskData[0]));
                epic.setType(type);
                epic.setTaskName(taskData[2]);
                epic.setDescription(taskData[4]);
                epic.setStatus(Status.valueOf(taskData[3]));
                return epic;

            case SUBTASK:
                Subtask subtask = new Subtask();
                subtask.setId(Integer.parseInt(taskData[0]));
                subtask.setType(type);
                subtask.setTaskName(taskData[2]);
                subtask.setDescription(taskData[4]);
                subtask.setStatus(Status.valueOf(taskData[3]));
                subtask.setEpicId(Integer.parseInt(taskData[5]));
                return subtask;
        }
        return null;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        //восстановление данных менеджера из файла при запуске программы
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager);

        BufferedReader br = new BufferedReader(new FileReader(file));
        br.readLine(); // пропускаем заголовок
        while (br.ready()) {
            String line = br.readLine();
            String[] splitter = line.split(",");
            TaskType type = TaskType.valueOf(splitter[1]);
            switch (type) {
                case TASK:
                    Task task = fromString(line);
                    tasks.put(task.getId(), task);
                    break;

                case EPIC:
                    Epic epic = (Epic) fromString(line);
                    epics.put(epic.getId(), epic);
                    break;

                case SUBTASK:
                    Subtask subtask = (Subtask) fromString(line);
                    subtasks.put(subtask.getId(), subtask);
                    Epic epicForSubtask = epics.get(subtask.getEpicId());
                    epicForSubtask.getSubtaskEpicsId().add(subtask.getId());
                    break;
            }
        }
        return manager;
    }
}