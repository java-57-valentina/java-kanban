package taskmanager;

import exception.TaskTimeConflictException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractTaskManagerTest<T extends TaskManager> {
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;
    protected T manager;

    @BeforeEach
    abstract void initManager();

    protected abstract T getTaskManagerForChecks();

    protected void createTasks() {
        try {
            task = manager.addTask(new Task("Задача1", "Описание задачи", Status.NEW, null, null));
            epic = manager.addEpic(new Epic("Эпик1", "Описание эпика"));
            subtask = manager.addSubtask(
                    new Subtask("Подзадача1", "Описание подзадачи", Status.IN_PROGRESS, epic.getId(), null, null));
        } catch (TaskTimeConflictException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addTask() {
        final String name = "Новая задача";
        final String description = "Подробное описание";
        final Status status = Status.DONE;

        int count = manager.getTasks().size();
        var ref = new Object() {
            Task added;
        };
        Assertions.assertDoesNotThrow( () ->
                ref.added = manager.addTask(new Task(name, description, status, null, null)));

        TaskManager taskManager = getTaskManagerForChecks();
        Task found = taskManager.getTask(ref.added.getId());

        assertNotNull(found);
        assertEquals(count + 1, taskManager.getTasks().size());
        assertEquals(ref.added, found);
    }



    @Test
    void addTaskNull() {
        final int tasksCount = manager.getTasks().size();

        var ref = new Object() {
            Task added;
        };

        Assertions.assertDoesNotThrow(() -> { ref.added = manager.addTask(null);});

        TaskManager taskManager = getTaskManagerForChecks();

        assertNull(ref.added);
        assertEquals(tasksCount, taskManager.getTasks().size());
    }

    @Test
    void addEpic() {
        int count = manager.getEpics().size();
        int id = manager.addEpic(new Epic("New epic", "Desc")).getId();

        TaskManager taskManager = getTaskManagerForChecks();
        Epic found = taskManager.getEpic(id);

        assertEquals(count + 1, taskManager.getEpics().size());
        assertNotNull(found);
        assertEquals(Status.NEW, found.getStatus());
    }

    @Test
    void addSubtask() {
        int count = manager.getEpics().size();
        var ref = new Object() {
            Task added = null;
        };
        Subtask newSubtask = new Subtask("New Subtask", "Desc", Status.NEW, epic.getId());
        Assertions.assertDoesNotThrow( () ->
            ref.added = manager.addSubtask(newSubtask));

        TaskManager taskManager = getTaskManagerForChecks();
        Epic found = taskManager.getEpic(epic.getId());

        assertEquals(count + 1, taskManager.getSubtasks().size());

        assertNotNull(taskManager.getSubtask(ref.added.getId()));
        assertEquals(manager.getSubtask(ref.added.getId()), taskManager.getSubtask(ref.added.getId()));
        assertEquals(2, found.getSubtasks().size());
        assertEquals(Status.IN_PROGRESS, found.getStatus());
    }

    @Test
    void addSubtaskWithInvalidEpicId() {
        final int subtasks = manager.getSubtasks().size();
        final int subtasksInEpic = epic.getSubtasks().size();

        Subtask newSubtask = new Subtask("New subtask", "Description", Status.NEW, 33);
        var ref = new Object() {
            Subtask added = null;
        };
        Assertions.assertDoesNotThrow( () -> ref.added = manager.addSubtask(newSubtask));

        TaskManager taskManager = getTaskManagerForChecks();
        Epic foundEpic = taskManager.getEpic(epic.getId());

        assertNull(ref.added);
        assertEquals(subtasks, taskManager.getSubtasks().size());
        assertEquals(subtasksInEpic, foundEpic.getSubtasks().size());
    }

    @Test
    void checkPrioritizedTasks() {
        manager.removeAllTasks();
        assertTrue(manager.getPrioritizedTasks().isEmpty());

        var ref = new Object() {
            int id1;
            int id2;
        };
        Assertions.assertDoesNotThrow( () -> {
            ref.id1 = manager.addTask(new Task("1", "", Status.NEW, LocalDateTime.of(2025, 2, 2, 12, 0), Duration.ofMinutes(15))).getId();
            ref.id2 = manager.addTask(new Task("2", "", Status.NEW, LocalDateTime.of(2025, 1, 2, 12, 0), Duration.ofMinutes(15))).getId();
        });
        TaskManager taskManager = getTaskManagerForChecks();
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(2, prioritizedTasks.size());
        assertEquals(ref.id2, prioritizedTasks.get(0).getId());
        assertEquals(ref.id1, prioritizedTasks.get(1).getId());
    }

    @Test
    void checkUniquenessOfId() {
        var ref = new Object() {
            Task task1;
            Task task2;
        };
        Assertions.assertDoesNotThrow( () -> {
            ref.task1 = manager.addTask(new Task("Name", "Description", Status.NEW));
            ref.task2 = manager.addTask(new Task("Name", "Description", Status.NEW));
        });
        assertNotEquals(ref.task1.getId(), ref.task2.getId());
    }

    @Test
    void removeAllTasks() {
        manager.removeAllTasks();

        TaskManager taskManager = getTaskManagerForChecks();

        assertNull(manager.getTask(task.getId()));
        assertNull(taskManager.getTask(task.getId()));
        assertEquals(0, manager.getTasks().size());
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void removeAllEpics() {
        manager.removeAllEpics();

        TaskManager taskManager = getTaskManagerForChecks();

        assertNull(taskManager.getEpic(epic.getId()));
        assertNull(taskManager.getSubtask(subtask.getId()));
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void removeAllSubtasks() {
        manager.removeAllSubtasks();
        TaskManager taskManager = getTaskManagerForChecks();

        assertNull(taskManager.getSubtask(subtask.getId()));
        assertNotNull(taskManager.getEpic(epic.getId()));
        assertEquals(Status.NEW, taskManager.getEpic(epic.getId()).getStatus());
        assertEquals(0, taskManager.getEpic(epic.getId()).getSubtasks().size());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void removeTask() {
        int count = manager.getTasks().size();
        manager.removeTask(task.getId());

        TaskManager taskManager = getTaskManagerForChecks();

        assertEquals(count - 1, taskManager.getTasks().size());
        assertNull(taskManager.getTask(task.getId()));
    }

    @Test
    void checkRemoveViewedTask() {
        final int tasksSize = manager.getTasks().size();

        manager.getTask(task.getId()); // view existing task
        List<Task> history = manager.getHistory();
        boolean viewedFound = history.contains(task);

        assertTrue(viewedFound);

        Task removed = manager.removeTask(task.getId()); // remove

        TaskManager taskManager = getTaskManagerForChecks();

        Task found = taskManager.getTask(removed.getId()); // must be null
        history = taskManager.getHistory();
        viewedFound = history.contains(task); // must be false

        assertEquals(tasksSize - 1, taskManager.getTasks().size());
        assertNull(found);
        assertFalse(viewedFound);
    }

    @Test
    void removeEpic() {
        int count = manager.getEpics().size();
        manager.removeEpic(epic.getId());

        TaskManager taskManager = getTaskManagerForChecks();

        assertEquals(count - 1, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasksByEpicId(epic.getId()).size());
        assertNull(taskManager.getEpic(epic.getId()));
    }

    @Test
    void removeSubtask() {
        int count = manager.getEpics().size();
        manager.removeSubtask(subtask.getId());

        TaskManager taskManager = getTaskManagerForChecks();

        assertEquals(count - 1, taskManager.getSubtasks().size());
        assertNull(taskManager.getSubtask(subtask.getId()));
    }

    @Test
    void updateTask() {
        final String newName = "Редактированное имя";
        final String newDescription = "Редактированное описание";
        final Status newStatus = Status.DONE;

        Task updated = task.clone();
        updated.setName(newName);
        updated.setDescription(newDescription);
        updated.setStatus(newStatus);
        Assertions.assertDoesNotThrow( () -> manager.updateTask(updated));

        TaskManager taskManager = getTaskManagerForChecks();
        Task found = taskManager.getTask(task.getId());

        assertNotNull(found);
        assertEquals(newName, found.getName());
        assertEquals(newDescription, found.getDescription());
        assertEquals(newStatus, found.getStatus());
    }

    @Test
    void updateEpic() {
        final String newName = "checkUpdateEpic";
        final String newDescription = "description";

        final Status oldStatus = epic.getStatus();
        final HashSet<Integer> oldSubtasks = manager.getEpic(epic.getId()).getSubtasks();

        Epic updated = epic.clone();
        updated.setName(newName);
        updated.setDescription(newDescription);
        updated.setStatus(Status.DONE); // статус вычисляется менеджером, это значение должно игнорироваться
        updated.addSubtask(22);     // должно игнорироваться

        manager.updateEpic(updated);
        TaskManager taskManager = getTaskManagerForChecks();

        Epic found = taskManager.getEpic(epic.getId());

        assertNotNull(found);
        assertEquals(newName, found.getName());
        assertEquals(newDescription, found.getDescription());
        assertEquals(oldStatus, found.getStatus());
        assertEquals(oldSubtasks, found.getSubtasks());  // список подзадач должен остаться прежним
    }

    @Test
    void updateSubtask() {
        final String newName = "checkUpdateSubtask";
        final String newDescription = "description";
        final Status newStatus = Status.IN_PROGRESS;
        final int oldEpicId = subtask.getEpicId();

        Subtask updated = subtask.clone();
        updated.setName(newName);
        updated.setDescription(newDescription);
        updated.setStatus(newStatus);
        updated.setEpicId(22);

        Assertions.assertDoesNotThrow( () ->
            manager.updateSubtask(updated));

        TaskManager taskManager = getTaskManagerForChecks();
        Subtask found = taskManager.getSubtask(subtask.getId());

        assertNotNull(found);
        assertEquals(newName, found.getName());
        assertEquals(newDescription, found.getDescription());
        assertEquals(newStatus, found.getStatus());
        assertEquals(oldEpicId, found.getEpicId());    // id эпика должен остаться прежним
    }

    @Test
    void checkHistoryShouldStoreOldVersionOfTask() {
        final int id = task.getId();

        Task oldVersion = manager.getTask(id).clone();
        Task update = new Task(id, "checkAddTaskToHistory", "", Status.DONE, null, null);
        Assertions.assertDoesNotThrow( () -> manager.updateTask(update));
        Task taskInHistory = manager.getHistory().getLast();

        assertEquals(oldVersion.getName(), taskInHistory.getName());
        assertEquals(oldVersion.getDescription(), taskInHistory.getDescription());
        assertEquals(oldVersion.getStatus(), taskInHistory.getStatus());
    }

    @Test
    void checkEpicStatusIfNoSubtasks() {
        Status expected = Status.NEW;

        manager.removeAllSubtasks();

        TaskManager taskManager = getTaskManagerForChecks();
        Epic found = taskManager.getEpic(epic.getId());

        assertEquals(expected, found.getStatus());
    }

    @Test
    void checkEpicStatusIfAllSubtasksAreNew() {
        Status expected = Status.NEW;

        manager.removeAllSubtasks();
        Assertions.assertDoesNotThrow( () -> {
            manager.addSubtask(new Subtask("Tmp1", "Desc", Status.NEW, epic.getId()));
            manager.addSubtask(new Subtask("Tmp2", "Desc", Status.NEW, epic.getId()));
            manager.addSubtask(new Subtask("Tmp3", "Desc", Status.NEW, epic.getId()));
        } );

        TaskManager taskManager = getTaskManagerForChecks();
        Epic found = taskManager.getEpic(epic.getId());

        assertEquals(expected, found.getStatus());
    }

    @Test
    void checkEpicStatusIfAllSubtasksAreDone() {
        Status expected = Status.DONE;

        manager.removeAllSubtasks();
        Assertions.assertDoesNotThrow( () -> {
                    manager.addSubtask(new Subtask("Tmp1", "Desc", Status.DONE, epic.getId()));
                    manager.addSubtask(new Subtask("Tmp2", "Desc", Status.DONE, epic.getId()));
                    manager.addSubtask(new Subtask("Tmp3", "Desc", Status.DONE, epic.getId()));
                });
        TaskManager taskManager = getTaskManagerForChecks();
        Epic found = taskManager.getEpic(epic.getId());

        assertEquals(expected, found.getStatus());
    }

    @Test
    void checkEpicStatusIfStatuses_NDD() {
        Status expected = Status.IN_PROGRESS;

        manager.removeAllSubtasks();
        Assertions.assertDoesNotThrow( ()-> {
            manager.addSubtask(new Subtask("Tmp1", "Desc", Status.NEW, epic.getId()));
            manager.addSubtask(new Subtask("Tmp2", "Desc", Status.DONE, epic.getId()));
            manager.addSubtask(new Subtask("Tmp3", "Desc", Status.DONE, epic.getId()));
        } );

        TaskManager taskManager = getTaskManagerForChecks();
        Epic found = taskManager.getEpic(epic.getId());

        assertEquals(expected, found.getStatus());
    }

    @Test
    void checkEpicStatusIfStatuses_NDP() {
        Status expected = Status.IN_PROGRESS;

        manager.removeAllSubtasks();
        Assertions.assertDoesNotThrow( () -> {
            manager.addSubtask(new Subtask("Tmp1", "Desc", Status.NEW, epic.getId()));
            manager.addSubtask(new Subtask("Tmp3", "Desc", Status.DONE, epic.getId()));
            manager.addSubtask(new Subtask("Tmp3", "Desc", Status.IN_PROGRESS, epic.getId()));
        });

        TaskManager taskManager = getTaskManagerForChecks();
        Epic found = taskManager.getEpic(epic.getId());

        assertEquals(expected, found.getStatus());
    }

    @Test
    void checkTaskStartTime() {
        LocalDateTime localTime = LocalDateTime.of(2025, 1, 10, 13, 13);
        Duration duration = Duration.ofMinutes(20);

        Task task = new Task("Test", "Desc", Status.NEW, localTime, duration);
        var ref = new Object() {
            int id = 0;
        };
        Assertions.assertDoesNotThrow( () -> ref.id = manager.addTask(task).getId());

        TaskManager taskManager = getTaskManagerForChecks();
        Task found = taskManager.getTask(ref.id);

        assertNotNull(found);
        assertEquals(found.getStartTime(), localTime);
    }

    @Test
    void checkTaskStartTimeNull() {
        Task task = new Task("Test", "Desc", Status.NEW);
        var ref = new Object() {
            int id = 0;
        };
        Assertions.assertDoesNotThrow( () -> ref.id = manager.addTask(task).getId());

        TaskManager taskManager = getTaskManagerForChecks();
        Task found = taskManager.getTask(ref.id);

        assertNotNull(found);
        assertNull(found.getStartTime());
    }

    @Test
    void checkTaskDuration() {
        long minutes = 20;

        Task task = new Task("Test", "Desc", Status.NEW);
        task.setDuration(Duration.ofMinutes(minutes));
        var ref = new Object() {
            int id = 0;
        };
        Assertions.assertDoesNotThrow( () -> ref.id = manager.addTask(task).getId());

        TaskManager taskManager = getTaskManagerForChecks();
        Task found = taskManager.getTask(ref.id);
        Duration duration = found.getDuration();

        assertNotNull(found);
        assertNotNull(duration);
        assertEquals(duration.toMinutes(), minutes);
    }

    @Test
    void checkTaskDurationNull() {
        Task task = new Task("Test", "Desc", Status.NEW);
        var ref = new Object() {
            int id = 0;
        };
        Assertions.assertDoesNotThrow( () -> ref.id = manager.addTask(task).getId());

        TaskManager taskManager = getTaskManagerForChecks();
        Task found = taskManager.getTask(ref.id);
        Duration duration = found.getDuration();

        assertNotNull(found);
        assertNull(duration);
    }

    @Test
    void checkTaskEndTime() {
        LocalDateTime localTime = LocalDateTime.of(2025, 1, 10, 13, 13);
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime expectedEndTime = localTime.plus(duration);

        Task task = new Task("Test", "Desc", Status.NEW);
        task.setStartTime(localTime);
        task.setDuration(duration);

        var ref = new Object() {
            int id;
        };
        Assertions.assertDoesNotThrow(() -> ref.id = manager.addTask(task).getId());

        TaskManager taskManager = getTaskManagerForChecks();
        Task found = taskManager.getTask(ref.id);
        LocalDateTime endTime = found.getEndTime();

        assertNotNull(found);
        assertNotNull(endTime);
        assertEquals(endTime, expectedEndTime);
    }

    @Test
    void checkSubtaskEndTime() {
        LocalDateTime localTime = LocalDateTime.of(2025, 1, 10, 13, 13);
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime expectedEndTime = localTime.plus(duration);

        Subtask task = new Subtask("Test", "Desc", Status.NEW, epic.getId());
        task.setStartTime(localTime);
        task.setDuration(duration);
        var ref = new Object() {
            int id;
        };
        Assertions.assertDoesNotThrow( () -> ref.id = manager.addSubtask(task).getId());

        TaskManager taskManager = getTaskManagerForChecks();
        Task found = taskManager.getSubtask(ref.id);
        LocalDateTime endTime = found.getEndTime();

        assertNotNull(found);
        assertNotNull(endTime);
        assertEquals(endTime, expectedEndTime);
    }

    @Test
    void checkEpicEndTimeFewSubtasks_1() {
        manager.removeAllSubtasks();

        LocalDateTime time_min = LocalDateTime.of(2025, 2, 18, 12, 30);
        LocalDateTime time_med = LocalDateTime.of(2025, 2, 19, 8, 10);
        LocalDateTime time_max = LocalDateTime.of(2025, 2, 20, 10, 20);

        Subtask subtask1 = new Subtask("Su1", "Des1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Su2", "Des2", Status.NEW, epic.getId());
        Subtask subtask3 = new Subtask("Su3", "Des3", Status.NEW, epic.getId());
        Subtask subtask4 = new Subtask("Su4", "Des4", Status.NEW, epic.getId());

        subtask1.setStartTime(time_max);
        subtask2.setStartTime(time_min);
        subtask3.setStartTime(time_med);
        subtask4.setStartTime(null);

        subtask1.setDuration(Duration.ofMinutes(30));
        subtask2.setDuration(Duration.ofMinutes(60));
        subtask3.setDuration(Duration.ofMinutes(10));
        subtask4.setDuration(null);

        final int expDuration = 30 + 60 + 10;
        final LocalDateTime exppectedEndTime = time_max.plus(Duration.ofMinutes(30));

        Assertions.assertDoesNotThrow( () -> manager.addSubtask(subtask1));
        Assertions.assertDoesNotThrow( () -> manager.addSubtask(subtask2));
        Assertions.assertDoesNotThrow( () -> manager.addSubtask(subtask3));
        Assertions.assertDoesNotThrow( () -> manager.addSubtask(subtask4));

        TaskManager taskManager = getTaskManagerForChecks();
        Epic found = taskManager.getEpic(epic.getId());
        LocalDateTime startTime = found.getStartTime();
        LocalDateTime endTime = found.getEndTime();
        Duration duration = found.getDuration();

        assertNotNull(startTime);
        assertNotNull(duration);
        assertEquals(time_min, startTime);
        assertEquals(exppectedEndTime, endTime);
        assertEquals(expDuration, duration.toMinutes());
    }

    @Test
    void checkEpicEndTimeFewSubtasksNullTime() {
        manager.removeAllSubtasks();

        Subtask subtask1 = new Subtask("Su1", "Des1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Su2", "Des2", Status.NEW, epic.getId());

        subtask1.setDuration(Duration.ofMinutes(30));
        subtask2.setDuration(Duration.ofMinutes(60));
        Assertions.assertDoesNotThrow( () -> manager.addSubtask(subtask1));
        Assertions.assertDoesNotThrow( () -> manager.addSubtask(subtask2));

        final int expDuration = 30 + 60;

        TaskManager taskManager = getTaskManagerForChecks();
        Epic found = taskManager.getEpic(epic.getId());
        LocalDateTime startTime = found.getStartTime();
        LocalDateTime endTime = found.getEndTime();
        Duration duration = found.getDuration();

        assertNull(startTime);
        assertNull(endTime);
        assertEquals(expDuration, duration.toMinutes());
    }

    @Test
    void checkEpicEndTimeNoSubtasks() {
        manager.removeAllSubtasks();

        TaskManager taskManager = getTaskManagerForChecks();
        Epic found = taskManager.getEpic(epic.getId());

        assertTrue(taskManager.getSubtasksByEpicId(epic.getId()).isEmpty());
        assertNotNull(found);
        assertNull(found.getStartTime());
        assertNull(found.getEndTime());
        assertNull(found.getDuration());
    }

    @Test
    void checkTaskEndTime_durationIsNull() {
        Task task = new Task("Test", "Desc", Status.NEW);
        task.setStartTime(LocalDateTime.of(2025, 1, 10, 13, 13));
        var ref = new Object() {
            int id;
        };
        Assertions.assertDoesNotThrow( () -> ref.id = manager.addTask(task).getId());

        TaskManager taskManager = getTaskManagerForChecks();
        Task found = taskManager.getTask(ref.id);
        LocalDateTime endTime = found.getEndTime();

        assertNotNull(found);
        assertNull(endTime);
    }

    @Test
    void checkTaskEndTime_startTimeIsNull() {
        Task task = new Task("Test", "Desc", Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        var ref = new Object() {
            int id;
        };

        Assertions.assertDoesNotThrow( () -> ref.id = manager.addTask(task).getId());

        TaskManager taskManager = getTaskManagerForChecks();
        Task found = taskManager.getTask(ref.id);
        LocalDateTime endTime = found.getEndTime();

        assertNotNull(found);
        assertNull(endTime);
    }

    @Test
    void checkTimeCollisions() {
        manager.removeAllTasks();
        manager.removeAllEpics();
        assertTrue(manager.getPrioritizedTasks().isEmpty());

        /* add first task */
        Task t1 = new Task("T1", "D1", Status.NEW, LocalDateTime.of(2025,1,1,12,0),
                Duration.ofMinutes(15));
        Assertions.assertDoesNotThrow( () -> manager.addTask(t1));
        assertEquals(1, manager.getPrioritizedTasks().size());
        assertEquals(1, manager.getTasks().size());

        /* try add other task (conflicting by time) */
        Task conflict = new Task("T", "D", Status.NEW, LocalDateTime.of(2025,1,1,12,5),
                Duration.ofMinutes(15));
        Assertions.assertThrows(TaskTimeConflictException.class,  () -> manager.addTask(conflict));
        assertEquals(1, manager.getPrioritizedTasks().size());
        assertEquals(1, manager.getTasks().size());

        /* add another task */
        Task t2 = new Task("T2", "D2", Status.NEW, LocalDateTime.of(2024,12,31,12,0),
                Duration.ofMinutes(30));
        Assertions.assertDoesNotThrow( () -> manager.addTask(t2));
        assertEquals(2, manager.getPrioritizedTasks().size());
        assertEquals(2, manager.getTasks().size());

        /* add another task */
        Task t3 = new Task("T2", "D2", Status.NEW, LocalDateTime.of(2024,12,31,20,0),
                Duration.ofMinutes(30));
        Assertions.assertDoesNotThrow( () -> manager.addTask(t3));
        assertEquals(3, manager.getPrioritizedTasks().size());
        assertEquals(3, manager.getTasks().size());
    }

}
