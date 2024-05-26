package service.managers;

import enums.Status;
import enums.TaskType;
import model.Epic;
import model.Subtask;
import model.Task;
import service.exception.ManagerSaveException;
import service.in_memory.InMemoryHistoryManager;
import service.in_memory.InMemoryTaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    //первая строка файла
    private static final String FIRST_STRING = "id,type,name,status,description,start_time, duration, end_time, epic";
    private static File file = new File("TASK_CSV"); //файл для сохранения данных

    public FileBackedTaskManager(HistoryManager historyManager) {
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
    public boolean deleteAllTasks() throws IOException { //автосохранение удаления всех задач
        boolean result = super.deleteAllTasks();
        save();
        return result;
    }

    @Override
    public boolean deleteAllEpics() { //автосохранение удаления всех эпиков и подзадач
        boolean result = super.deleteAllEpics();
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public boolean deleteAllSubtasks() { //автосохранение удаления всех подзадач
        boolean result = super.deleteAllSubtasks();
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Task createTask(Task task) throws IOException { //автосохранение создания задачи
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) throws IOException { //автосохранение создания эпика
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws IOException { //автосохранение создания подзадачи
        Subtask newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public void updateTask(Task newTask) throws IOException { //автосохранение обновления задачи
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) throws IOException { //автосохранение обновления эпика
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) throws IOException { //автосохранение обновления подзадачи
        super.updateSubtask(newSubtask);
        save();
    }

    @Override
    public void deleteByTaskId(int id) throws IOException { //автосохранение удаления задачи по айди
        super.deleteByTaskId(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) throws IOException { //автосохранение удаления эпика по айди
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) { //автосохранение удаления подзадачи по айди
        super.deleteSubtaskById(id);
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Task> getHistory() { //возвращение списка просмотренных задач
        return super.getHistory();
    }

    public void save() throws IOException { //сохранение текущего состояния менеджера в указанный файл
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write(FIRST_STRING + "\n");

            Stream.concat((Stream.concat(getAllTasks().stream(), getAllEpics().stream())),
                            getAllSubtasks().stream())
                    .forEach(task -> {
                        try {
                            bufferedWriter.write(task.toString() + System.lineSeparator());
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка сохранения текущего состояния менеджера в файл", e);
                        }
                    });

            for (Task task : getAllTasks()) {
                bufferedWriter.write(toString(task));
            }

            for (Epic epic : getAllEpics()) {
                bufferedWriter.write(toString(epic));
            }

            for (Subtask subtask : getAllSubtasks()) {
                bufferedWriter.write(toString(subtask));
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения текущего состояния менеджера в файл", e);
        }
    }

    public String toString(Task task) { //сохранение задачи в строку
        String result = switch (task.getType()) {
            case TASK -> task.getId() + ",TASK," + task.getTaskName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getStartTime() + "," + task.getDuration() + ","
                    + task.getEndTime() + "\n";
            case EPIC -> task.getId() + ",EPIC," + task.getTaskName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getStartTime() + "," + task.getDuration() + ","
                    + task.getEndTime() + "\n";
            case SUBTASK -> task.getId() + ",SUBTASK," + task.getTaskName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getStartTime() + "," + task.getDuration() + ","
                    + task.getEndTime() + "," + ((Subtask) task).getEpicId() + "\n";
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
                task.setStartTime(LocalDateTime.parse(taskData[5]));
                task.setDuration(Duration.parse(taskData[6]));
                task.setEndTime(LocalDateTime.parse(taskData[7]));
                return task;

            case EPIC:
                Epic epic = new Epic();
                epic.setId(Integer.parseInt(taskData[0]));
                epic.setType(type);
                epic.setTaskName(taskData[2]);
                epic.setDescription(taskData[4]);
                epic.setStatus(Status.valueOf(taskData[3]));
                epic.setStartTime(LocalDateTime.parse(taskData[5]));
                epic.setDuration(Duration.parse(taskData[6]));
                epic.setEndTime(LocalDateTime.parse(taskData[7]));
                return epic;

            case SUBTASK:
                Subtask subtask = new Subtask();
                subtask.setId(Integer.parseInt(taskData[0]));
                subtask.setType(type);
                subtask.setTaskName(taskData[2]);
                subtask.setDescription(taskData[4]);
                subtask.setStatus(Status.valueOf(taskData[3]));
                subtask.setStartTime(LocalDateTime.parse(taskData[5]));
                subtask.setDuration(Duration.parse(taskData[6]));
                subtask.setEndTime(LocalDateTime.parse(taskData[7]));
                subtask.setEpicId(Integer.parseInt(taskData[8]));
                return subtask;
        }
        return null;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        //восстановление данных менеджера из файла при запуске программы
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager());
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        try {
            br.readLine(); // пропускаем заголовок
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        while (true) {
            try {
                if (!br.ready()) break;
            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage());
            }
            String line = null;
            try {
                line = br.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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