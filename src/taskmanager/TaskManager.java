package taskmanager;

import exception.TaskTimeConflictException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    Task addTask(Task task) throws TaskTimeConflictException;

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask) throws TaskTimeConflictException;


    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    List<Task> getTasks();

    List<Task> getPrioritizedTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getSubtasksByEpicId(int epicId);


    @SuppressWarnings("unused")
    Status getTaskStatus(int id);

    @SuppressWarnings("unused")
    Status getEpicStatus(int id);

    Status getSubtaskStatus(int id);


    int removeAllTasks();

    int removeAllEpics();

    int removeAllSubtasks();


    Task removeTask(int id);

    Epic removeEpic(int id);

    Subtask removeSubtask(int id);


    Task updateTask(Task task) throws TaskTimeConflictException;

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask) throws TaskTimeConflictException;

    List<Task> getHistory();
}
