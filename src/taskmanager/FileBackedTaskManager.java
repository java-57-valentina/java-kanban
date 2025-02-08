package taskmanager;

import exception.LoadTaskException;
import exception.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;

    private FileBackedTaskManager(Path path) {
        super();
        this.path = path;
    }

    @Override
    public Task addTask(Task task) {
        Task added = super.addTask(task);
        save();
        return added;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic added = super.addEpic(epic);
        save();
        return added;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask added = super.addSubtask(subtask);
        save();
        return added;
    }

    @Override
    public int removeAllTasks() {
        int result = super.removeAllTasks();
        save();
        return result;
    }

    @Override
    public int removeAllEpics() {
        int result = super.removeAllEpics();
        save();
        return result;
    }

    @Override
    public int removeAllSubtasks() {
        int result = super.removeAllSubtasks();
        save();
        return result;
    }

    @Override
    public Task removeTask(int id) {
        Task removed = super.removeTask(id);
        save();
        return removed;
    }

    @Override
    public Epic removeEpic(int id) {
        Epic removed = super.removeEpic(id);
        save();
        return removed;
    }

    @Override
    public Subtask removeSubtask(int id) {
        Subtask removed = super.removeSubtask(id);
        save();
        return removed;
    }

    @Override
    public Task updateTask(Task task) {
        Task updated = super.updateTask(task);
        save();
        return updated;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updated = super.updateEpic(epic);
        save();
        return updated;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updated = super.updateSubtask(subtask);
        save();
        return updated;
    }

    protected void save() throws ManagerSaveException {
        final String title = "type, id, name, description, status, links\n";

        try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile(), false)) {

            fileOutputStream.write(title.getBytes(StandardCharsets.UTF_8));

            for (Task item : tasks.values()) {
                fileOutputStream.write(item.toString().getBytes(StandardCharsets.UTF_8));
            }
            for (Task item : epics.values()) {
                fileOutputStream.write(item.toString().getBytes(StandardCharsets.UTF_8));
            }
            for (Task item : subtasks.values()) {
                fileOutputStream.write(item.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные в файл '" + path.getFileName() + "'");
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) throws LoadTaskException {

        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8))) {

            reader.readLine(); // skip first line (header)
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                Task item = fromString(line);
                manager.uniqueId = Math.max(manager.uniqueId, item.getId()); // Счетчик id не должен быть меньше id какого-либо таска

                if (item instanceof Epic) {
                    manager.addEpicImpl((Epic) item);
                } else if (item instanceof Subtask) {
                    manager.addSubtaskImpl((Subtask) item);
                } else {
                    manager.addTaskImpl(item);
                }
            }
            return manager;
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            throw new RuntimeException("Произошла ошибка чтения задач из файла '" + path.getFileName() + "'");
        }
    }

    private static Task fromString(String line) throws LoadTaskException {
        String[] parts = line.split(", ");
        if (parts.length == 0 || line.isBlank())
            throw new LoadTaskException("Неверный формат строки для десериализации задачи");

        TaskType taskType;
        try {
            taskType = TaskType.valueOf(parts[0]);
        } catch (IllegalArgumentException e) {
            throw new LoadTaskException("Неподдерживаемый тип задачи " + parts[0]);
        }

        switch (taskType) {
            case TASK -> {
                return taskFromString(parts);
            }
            case EPIC -> {
                return epicFromString(parts);
            }
            case SUBTASK -> {
                return subtaskFromString(parts);
            }
            default -> throw new LoadTaskException("Неподдерживаемый тип задачи " + taskType);
        }
    }

    private static Subtask subtaskFromString(String[] parts) throws LoadTaskException {
        if (parts.length < 6)
            throw new LoadTaskException("Неверный формат строки");

        try {
            int id = Integer.parseInt(parts[1]);
            int epicId = Integer.parseInt(parts[5]);
            String name = parts[2];
            String desk = parts[3];
            Status status = Status.valueOf(parts[4]);
            return new Subtask(id, name, desk, status, epicId);
        } catch (IllegalArgumentException e) {
            throw new LoadTaskException("Неподдерживаемый формат строки: " + String.join(",", parts));
        }
    }

    private static Epic epicFromString(String[] parts) throws LoadTaskException {
        if (parts.length < 4)
            throw new LoadTaskException("Неверный формат строки");

        try {
            int id = Integer.parseInt(parts[1]);
            String name = parts[2];
            String desk = parts[3];
            return new Epic(id, name, desk);
        } catch (IllegalArgumentException e) {
            throw new LoadTaskException("Неподдерживаемый формат строки: " + String.join(",", parts));
        }
    }

    private static Task taskFromString(String[] parts) throws LoadTaskException {
        if (parts.length < 5)
            throw new LoadTaskException("Неверный формат строки");

        try {
            int id = Integer.parseInt(parts[1]);
            String name = parts[2];
            String desk = parts[3];
            Status status = Status.valueOf(parts[4]);
            return new Task(id, name, desk, status);
        } catch (IllegalArgumentException e) {
            throw new LoadTaskException("Неподдерживаемый формат строки: " + String.join(",", parts));
        }
    }
}
