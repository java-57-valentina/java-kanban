package taskmanager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {
    private int uniqueId = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        uniqueId = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }


    public Task addTask(Task task) {
        if (task == null)
            return null;
        task.setId(nextId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic addEpic(Epic epic) {
        if (epic == null)
            return null;
        epic.setId(nextId());
        epic.removeAllSubtasks(); // для консистентности
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask addSubtask(Subtask subtask) {
        if (subtask == null)
            return null;

        final Epic epic = epics.get(subtask.getEpicId());
        if (epic == null)
            return null;

        subtask.setId(nextId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic);
        return subtask;
    }


    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }
    
    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public List<Task> getTasks() {
        return tasks.values().stream().toList();
    }

    public List<Epic> getEpics() {
        return epics.values().stream().toList();
    }
    
    public List<Subtask> getSubtasks() {
        return subtasks.values().stream().toList();
    }
    
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        return subtasks.values().stream().filter(c -> c.getEpicId() == epicId).collect(Collectors.toList());
    }


    public Status getTaskStatus(int id) {
        Task item = tasks.get(id);
        return (item != null) ? item.getStatus() : Status.UNDEFINED;
    }
    public Status getEpicStatus(int id) {
        Task item = epics.get(id);
        return (item != null) ? item.getStatus() : Status.UNDEFINED;
    }
    public Status getSubtaskStatus(int id) {
        Task item = subtasks.get(id);
        return (item != null) ? item.getStatus() : Status.UNDEFINED;
    }


    public int removeAllTasks() {
        int count = tasks.size();
        tasks.clear();
        return count;
    }

    public int removeAllEpics() {
        int count = epics.size();
        epics.clear();
        subtasks.clear();
        return count;
    }

    public int removeAllSubtasks() {
        int count = subtasks.size();
        subtasks.clear();
        for (Epic e : epics.values()) {
            e.removeAllSubtasks();
            updateEpicStatus(e);
        }
        return count;
    }


    public Task removeTask(int id) {
        return tasks.remove(id);
    }

    public Epic removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            subtasks.values().removeIf(c -> c.getEpicId() == id);
        }
        return epic;
    }

    public Subtask removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null)
            return null;
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtask(id);
            updateEpicStatus(epic);
        }
        return subtask;
    }


    public Task updateTask(Task task) {
        if (tasks.replace(task.getId(), task) == null) {
            return null;
        }
        return task; // return object in actual state
    }

    public Epic updateEpic(Epic epic) {
        if (epic == null)
            return null;

        Epic old = epics.get(epic.getId());
        if (old == null)
            return null;

        if (epic.equals(old)) {
            return old;
        }

        /* We don't update epic's list of subtasks
        all changes in subtasks must be using addSubtask/removeSubtask */
        epic.removeAllSubtasks();
        for (int id : old.getSubtasks())
            epic.addSubtask(id);

        epics.replace(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if (subtask == null)
            return null;

        final int id = subtask.getId();
        if (subtask.equals(subtasks.get(id))) {
            return subtasks.get(id);
        }

        final Subtask oldValue = subtasks.get(id);
        if (oldValue == null) {
            return null;
        }

        /* We ignore the change of 'epicId' in subtask.
        use addSubtask/removeSubtask to change epic of subtask */
        if (subtask.getEpicId() != oldValue.getEpicId()) {
            subtask.setEpicId(oldValue.getEpicId());
        }

        subtasks.replace(id, subtask);
        updateEpicStatus(getEpic(subtask.getEpicId()));
        return subtask; // return object in actual state
    }


    private Status calculateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            return Status.NEW;
        }

        final Status firstSubtaskStatus = getSubtaskStatus(epic.getSubtasks().iterator().next());
        if (firstSubtaskStatus == Status.IN_PROGRESS) {
            return Status.IN_PROGRESS;
        }
        for (int id : epic.getSubtasks()) {
            if (getSubtaskStatus(id) != firstSubtaskStatus) {
                return Status.IN_PROGRESS;
            }
        }
        return firstSubtaskStatus;
    }

    protected void updateEpicStatus(Epic epic) {
        epic.setStatus(calculateEpicStatus(epic));
    }

    private int nextId() {
        return ++uniqueId;
    }
}
