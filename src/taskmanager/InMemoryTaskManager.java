package taskmanager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int uniqueId = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final ArrayList<Task> viewedTasks;

    public InMemoryTaskManager() {
        uniqueId = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        viewedTasks = new ArrayList<>();
    }

    @Override
    public Task addTask(Task task) {
        if (task == null)
            return null;
        task.setId(nextId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        if (epic == null)
            return null;
        epic.setId(nextId());
        epic.removeAllSubtasks(); // для консистентности
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
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


    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        addToHistory(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        addToHistory(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        addToHistory(subtask);
        return subtask;
    }

    @Override
    public List<Task> getTasks() {
        return tasks.values().stream().toList();
    }

    @Override
    public List<Epic> getEpics() {
        return epics.values().stream().toList();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return subtasks.values().stream().toList();
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        return subtasks.values().stream().filter(c -> c.getEpicId() == epicId).collect(Collectors.toList());
    }


    @Override
    public Status getTaskStatus(int id) {
        Task item = tasks.get(id);
        return (item != null) ? item.getStatus() : Status.UNDEFINED;
    }

    @Override
    public Status getEpicStatus(int id) {
        Task item = epics.get(id);
        return (item != null) ? item.getStatus() : Status.UNDEFINED;
    }

    @Override
    public Status getSubtaskStatus(int id) {
        Task item = subtasks.get(id);
        return (item != null) ? item.getStatus() : Status.UNDEFINED;
    }


    @Override
    public int removeAllTasks() {
        int count = tasks.size();
        tasks.clear();
        return count;
    }

    @Override
    public int removeAllEpics() {
        int count = epics.size();
        epics.clear();
        subtasks.clear();
        return count;
    }

    @Override
    public int removeAllSubtasks() {
        int count = subtasks.size();
        subtasks.clear();
        for (Epic e : epics.values()) {
            e.removeAllSubtasks();
            updateEpicStatus(e);
        }
        return count;
    }


    @Override
    public Task removeTask(int id) {
        Task removed = tasks.remove(id);
        return removed;
    }

    @Override
    public Epic removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            subtasks.values().removeIf(c -> c.getEpicId() == id);
        }
        return epic;
    }

    @Override
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


    @Override
    public Task updateTask(Task task) {
        if (tasks.replace(task.getId(), task) == null) {
            return null;
        }
        return task; // return object in actual state
    }

    @Override
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

    @Override
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

        /* Мы не обрабатываем ситуацию, когда у подзадачи меняется родительский эпик.
        Обновляем все остальные данные, а id эпика остается прежним */
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
    
    private void addToHistory(Task task) {
        historyManager.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
