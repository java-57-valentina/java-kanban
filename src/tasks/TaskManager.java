package tasks;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {
    Counter counter;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        counter = new Counter();
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }


    public Task addTask(Task task) {
        if (task == null)
            return null;
        task.setId(counter.nextValue());
        tasks.put(task.getId(), task);
        System.out.println("addTask: " + task);
        return task;
    }

    public Epic addEpic(Epic epic) {
        if (epic == null)
            return null;
        epic.setId(counter.nextValue());
        epic.removeAllSubtasks(); // для консистентности
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
        System.out.println("addEpic: " + epic);
        return epic;
    }

    public Subtask addSubtask(Subtask subtask) {
        if (subtask == null)
            return null;

        final Epic epic = epics.get(subtask.getEpicId());
        if (epic == null)
            return null;

        subtask.setId(counter.nextValue());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic);
        System.out.println("addSubtask " + subtask);
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

    public List<Subtask> getSubtasksByEpic(Epic epic) {
        if (epic == null)
            return null;
        return getSubtasksByEpicId(epic.getId());
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

        /* We don't update list of subtasks in epic
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
        final Subtask oldValue = subtasks.get(id);
        if (oldValue == null) {
            return null; // use 'addSubtask' to create subtask
        }

        if (subtask.getEpicId() != oldValue.getEpicId()) {
            subtask.setEpicId(oldValue.getEpicId()); // use 'addSubtask/removeSubtask' to change epic of subtask
        }

        subtasks.replace(id, subtask);
        updateEpicStatus(getEpic(subtask.getEpicId()));
        return subtask; // return object in actual state
    }


    protected Status calculateEpicStatus(Epic epic) {
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
}

class Counter {
    private int value = 0;
    
    public int nextValue() {
        return ++value;
    }
}
