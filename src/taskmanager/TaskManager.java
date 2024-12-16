package taskmanager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);


    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getSubtasksByEpicId(int epicId);


    Status getTaskStatus(int id);

    Status getEpicStatus(int id);

    Status getSubtaskStatus(int id);


    int removeAllTasks();

    int removeAllEpics();

    int removeAllSubtasks();


    Task removeTask(int id);

    Epic removeEpic(int id);

    Subtask removeSubtask(int id);


    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);


    List<Task> getHistory();
}
