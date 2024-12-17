package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    InMemoryTaskManager manager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void initManager() {
        HistoryManager defaultHistory = Managers.getDefaultHistory();
        manager = new InMemoryTaskManager(defaultHistory);
        task = manager.addTask(new Task("Task Name", "Task description", Status.NEW));
        epic = manager.addEpic(new Epic("Epic Name", "Epic description"));
        subtask = manager.addSubtask(new Subtask("Subtask Name", "Subtask description", Status.NEW, epic.getId()));
    }

    @Test
    void addTask() {
        final int tasks = manager.getTasks().size();

        Task added = manager.addTask(new Task("NewTask", "Description", Status.NEW));
        Task found = manager.getTask(added.getId());

        assertNotNull(added);
        assertTrue(added.getId() > 0);
        assertTrue(manager.getTasks().contains(added));
        assertEquals(tasks + 1, manager.getTasks().size());
        assertNotNull(found);
        assertEquals(added.getId(), found.getId());

        /* Тест "проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера"
        невозможно выполнить, так как менеджер в любом случае переопределяет Id таски */
    }

    @Test
    void addTaskNull() {
        final int tasks = manager.getTasks().size();

        Task added = manager.addTask(null);

        assertNull(added);
        assertEquals(tasks, manager.getTasks().size());
    }

    @Test
    void addEpic() {
        final int epics = manager.getEpics().size();

        Epic added = manager.addEpic(new Epic("New epic", "Description"));
        Epic found = manager.getEpic(added.getId());

        assertNotNull(added);
        assertNotNull(found);
        assertTrue(added.getId() > 0);
        assertTrue(manager.getEpics().contains(added));
        assertEquals(epics + 1, manager.getEpics().size());
        assertEquals(added.getId(), found.getId());
    }

    @Test
    void addSubtask() {
        final int subtasks = manager.getSubtasks().size();
        final int subtasksInEpic = epic.getSubtasks().size();

        Subtask added = manager.addSubtask(new Subtask("New subtask", "Description", Status.NEW, epic.getId()));
        Subtask found = manager.getSubtask(added.getId());

        assertNotNull(added);
        assertNotNull(found);
        assertTrue(added.getId() > 0);
        assertTrue(manager.getSubtasks().contains(added));
        assertEquals(subtasks + 1, manager.getSubtasks().size());
        assertEquals(subtasksInEpic + 1, epic.getSubtasks().size());
        assertEquals(added.getId(), found.getId());

        /* Тест "проверьте, что объект Subtask нельзя сделать своим же эпиком"
        невозможно выполнить, так как данная ситуация невозможна на уровне компиляции из-за несоответствия типов */
    }

    @Test
    void addSubtaskWithInvalidEpicId() {
        final int subtasks = manager.getSubtasks().size();
        final int subtasksInEpic = epic.getSubtasks().size();

        Subtask subtask = manager.addSubtask(new Subtask("New subtask", "Description", Status.NEW, 33));

        assertNull(subtask);
        assertEquals(subtasks, manager.getSubtasks().size());
        assertEquals(subtasksInEpic, epic.getSubtasks().size());
    }

    @Test
    void checkUniquenessOfId() {
        Task task1 = manager.addTask(new Task("Name", "Description", Status.NEW));
        Task task2 = manager.addTask(new Task("Name", "Description", Status.NEW));

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void checkRemoveTask() {
        final int tasks = manager.getTasks().size();

        Task removed = manager.removeTask(task.getId());
        Task found = manager.getTask(removed.getId());

        assertEquals(tasks - 1, manager.getTasks().size());
        assertNull(found);
    }

    @Test
    void checkRemoveEpic() {
        final int epics = manager.getEpics().size();
        final int subtasks = manager.getSubtasks().size();
        final int subtasksInEpic = epic.getSubtasks().size();

        manager.removeEpic(epic.getId());

        assertEquals(epics - 1,manager.getEpics().size());
        assertEquals(subtasks - subtasksInEpic, manager.getSubtasks().size());
    }

    @Test
    void checkRemoveSubtask() {
        final int epicId = subtask.getEpicId();
        final int subtaskId = subtask.getId();

        manager.removeSubtask(subtask.getId());

        assertTrue(manager.getSubtasks().isEmpty());
        assertFalse(manager.getEpic(epicId).getSubtasks().contains(subtaskId));
    }

    @Test
    void checkRemoveAllTasks() {
        manager.removeAllTasks();
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    void checkRemoveAllEpics() {
        manager.removeAllEpics();

        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    void checkRemoveAllSubtasks() {
        manager.removeAllEpics();
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    void checkTaskFieldsAfterAdding() {
        final String name = "Новая задача";
        final String description = "Подробное описание";
        final Status status = Status.DONE;

        Task added = manager.addTask(new Task(name, description, status));
        Task found = manager.getTask(added.getId());

        assertEquals(name, found.getName());
        assertEquals(description, found.getDescription());
        assertEquals(status, found.getStatus());
    }

    @Test
    void checkHistoryShouldStoreOldVersionOfTask() {
        final int taskId = task.getId();

        Task oldVersion = manager.getTask(taskId).clone();
        manager.updateTask(new Task(taskId, "checkAddTaskToHistory", "", Status.DONE));
        Task taskInHistory = manager.getHistory().getLast();

        assertEquals(oldVersion.getName(), taskInHistory.getName());
        assertEquals(oldVersion.getDescription(), taskInHistory.getDescription());
        assertEquals(oldVersion.getStatus(), taskInHistory.getStatus());
    }

    @Test
    void checkUpdateTask() {
        final String newName = "Редактированное имя";
        final String newDescription = "Редактированное описание";
        final Status newStatus = Status.DONE;

        Task updated = new Task(task.getId(), newName, newDescription, newStatus);
        manager.updateTask(updated);
        Task found = manager.getTask(task.getId());

        assertNotNull(found);
        assertEquals(newName, found.getName());
        assertEquals(newDescription, found.getDescription());
        assertEquals(newStatus, found.getStatus());
    }

    @Test
    void checkUpdateEpic() {
        final String newName = "checkUpdateEpic";
        final String newDescription = "description";
        final Status oldStatus = epic.getStatus();
        final HashSet<Integer> oldSubtasks = epic.getSubtasks();

        Epic updated = epic.clone();
        updated.setName(newName);
        updated.setDescription(newDescription);
        updated.setStatus(Status.DONE); // статус вычисляется менеджером, это значение должно игнорироваться
        updated.addSubtask(22);

        manager.updateEpic(updated);
        Epic found = manager.getEpic(epic.getId());

        assertNotNull(found);
        assertEquals(newName, found.getName());
        assertEquals(newDescription, found.getDescription());
        assertEquals(oldStatus, found.getStatus());
        assertEquals(oldSubtasks, found.getSubtasks());  // список подзадач должен остаться прежним
    }

    @Test
    void checkUpdateSubtask() {
        final String newName = "checkUpdateEpic";
        final String newDescription = "description";
        final Status newStatus = Status.IN_PROGRESS;
        final int oldEpicId = subtask.getEpicId();

        Subtask updated = subtask.clone();
        updated.setName(newName);
        updated.setDescription(newDescription);
        updated.setStatus(newStatus);
        updated.setEpicId(22);

        manager.updateSubtask(updated);
        Subtask found = manager.getSubtask(subtask.getId());

        assertNotNull(found);
        assertEquals(newName, found.getName());
        assertEquals(newDescription, found.getDescription());
        assertEquals(newStatus, found.getStatus());
        assertEquals(oldEpicId, found.getEpicId());    // id эпика должен остаться прежним
    }

    @Test
    void checkEpicStatusIfNoSubtasks() {
        manager.removeAllSubtasks();
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void checkEpicStatusIfAllSubtasksAreNew() {
        Status expected = Status.NEW;

        manager.removeAllSubtasks();
        manager.addSubtask(new Subtask("Tmp1", "Desc", Status.NEW, epic.getId()));
        manager.addSubtask(new Subtask("Tmp2", "Desc", Status.NEW, epic.getId()));
        manager.addSubtask(new Subtask("Tmp3", "Desc", Status.NEW, epic.getId()));

        assertEquals(expected, epic.getStatus());
    }

    @Test
    void checkEpicStatusIfAllSubtasksAreDone() {
        Status expected = Status.DONE;

        manager.removeAllSubtasks();
        manager.addSubtask(new Subtask("Tmp1", "Desc", Status.DONE, epic.getId()));
        manager.addSubtask(new Subtask("Tmp2", "Desc", Status.DONE, epic.getId()));
        manager.addSubtask(new Subtask("Tmp3", "Desc", Status.DONE, epic.getId()));

        assertEquals(expected, epic.getStatus());
    }

    @Test
    void checkEpicStatusIfStatuses_NDD() {
        Status expected = Status.IN_PROGRESS;

        manager.removeAllSubtasks();
        manager.addSubtask(new Subtask("Tmp1", "Desc", Status.NEW, epic.getId()));
        manager.addSubtask(new Subtask("Tmp2", "Desc", Status.DONE, epic.getId()));
        manager.addSubtask(new Subtask("Tmp3", "Desc", Status.DONE, epic.getId()));

        assertEquals(expected, epic.getStatus());
    }

    @Test
    void checkEpicStatusIfStatuses_NDP() {
        Status expected = Status.IN_PROGRESS;

        manager.removeAllSubtasks();
        manager.addSubtask(new Subtask("Tmp1", "Desc", Status.NEW, epic.getId()));
        manager.addSubtask(new Subtask("Tmp3", "Desc", Status.DONE, epic.getId()));
        manager.addSubtask(new Subtask("Tmp3", "Desc", Status.IN_PROGRESS, epic.getId()));

        assertEquals(expected, epic.getStatus());
    }
}