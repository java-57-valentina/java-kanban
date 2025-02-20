package taskmanager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HistoryManager historyManager;
    protected int uniqueId = 0;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;

    public InMemoryTaskManager() {
        uniqueId = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public Task addTask(Task task) {
        if (task == null)
            return null;
        task.setId(nextId());
        return addTaskImpl(task);
    }

    protected Task addTaskImpl(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        if (epic == null)
            return null;
        epic.setId(nextId());
        return addEpicImpl(epic);
    }

    protected Epic addEpicImpl(Epic epic) {
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
        return addSubtaskImpl(subtask);
    }

    protected Subtask addSubtaskImpl(Subtask subtask) {
        final Epic epic = epics.get(subtask.getEpicId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic);
        updateEpicTime(epic);
        return subtask;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
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
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();

        return count;
    }

    @Override
    public int removeAllEpics() {
        int count = epics.size();
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        return count;
    }

    @Override
    public int removeAllSubtasks() {
        int count = subtasks.size();
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic e : epics.values()) {
            e.removeAllSubtasks();
            updateEpicStatus(e);
            updateEpicTime(e);
        }
        return count;
    }


    @Override
    public Task removeTask(int id) {
        Task removed = tasks.remove(id);
        if (removed == null)
            return null;

        historyManager.remove(removed.getId());
        return removed;
    }

    @Override
    public Epic removeEpic(int id) {
        Epic removed = epics.remove(id);
        if (removed == null)
            return null;

        historyManager.remove(removed.getId());
        for (Integer sid : removed.getSubtasks())
            historyManager.remove(sid);

        subtasks.values().removeIf(c -> c.getEpicId() == id);
        return removed;
    }

    @Override
    public Subtask removeSubtask(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed == null)
            return null;

        historyManager.remove(id);
        Epic epic = epics.get(removed.getEpicId());
        if (epic != null) {
            epic.removeSubtask(id);
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
        return removed;
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
        updateEpicTime(epic);
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
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
        updateEpicTime(epic);
        return subtask; // return object in actual state
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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

    public void updateEpicTime(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
            return;
        }

        Optional<LocalDateTime> startTime = epic.getSubtasks().stream()
                .map(this::getSubtask)
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> endTime = epic.getSubtasks().stream()
                .map(this::getSubtask)
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        Duration duration = epic.getSubtasks().stream()
                .map(this::getSubtask)
                .map(Task::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(startTime.orElse(null));
        epic.setEndTime(endTime.orElse(null));
        epic.setDuration(duration);
    }

    private int nextId() {
        return ++uniqueId;
    }
}
