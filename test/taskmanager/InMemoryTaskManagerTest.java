package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    InMemoryTaskManager manager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void initManager() {
        manager = new InMemoryTaskManager();
        task = manager.addTask(new Task("Task Name", "Task description", Status.NEW));
        epic = manager.addEpic(new Epic("Epic Name", "Epic description"));
        subtask = manager.addSubtask(new Subtask("Subtask Name", "Subtask description", Status.NEW, epic.getId()));
    }

    @Test
    void addTask() {
        int tasks = manager.getTasks().size();
        Task added = manager.addTask(new Task("NewTask", "Description", Status.NEW));

        assertNotNull(added);
        assertTrue(added.getId() > 0);
        assertTrue(manager.getTasks().contains(added));
        assertEquals(tasks + 1, manager.getTasks().size());

        Task found = manager.getTask(added.getId());

        assertNotNull(found);
        assertEquals(added.getId(), found.getId());

        /* Тест "проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера"
        невозможно выполнить, так как менеджер в любом случае переопределяет Id таски */
    }

    @Test
    void addEpic() {
        final int epics = manager.getEpics().size();
        Epic added = manager.addEpic(new Epic("New epic", "Description"));

        assertNotNull(added);
        assertTrue(added.getId() > 0);
        assertTrue(manager.getEpics().contains(added));
        assertEquals(epics + 1, manager.getEpics().size());

        Epic found = manager.getEpic(added.getId());

        assertNotNull(found);
        assertEquals(added.getId(), found.getId());
    }

    @Test
    void addSubtask() {
        int subtasks = manager.getSubtasks().size();
        int subtasksInEpic = epic.getSubtasks().size();
        Subtask added = manager.addSubtask(new Subtask("New subtask", "Description", Status.NEW, epic.getId()));

        assertNotNull(added);
        assertTrue(added.getId() > 0);
        assertTrue(manager.getSubtasks().contains(added));
        assertEquals(subtasks + 1, manager.getSubtasks().size());
        assertEquals(subtasksInEpic + 1, epic.getSubtasks().size());

        Subtask found = manager.getSubtask(added.getId());

        assertNotNull(found);
        assertEquals(added.getId(), found.getId());

        /* Тест "проверьте, что объект Subtask нельзя сделать своим же эпиком"
        невозможно выполнить, так как данная ситуация невозможна на уровне компиляции из-за несоответствия типов */
    }

    @Test
    void addSubtaskWithInvalidEpicId() {
        int subtasks = manager.getSubtasks().size();
        int subtasksInEpic = epic.getSubtasks().size();
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
        int tasks = manager.getTasks().size();
        Task removed = manager.removeTask(task.getId());

        assertEquals(tasks - 1, manager.getTasks().size());

        Task found = manager.getTask(removed.getId());

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
        String name = "Новая задача";
        String description = "Подробное описание";
        Status status = Status.DONE;

        Task newTask = new Task(name, description, status);
        Task added = manager.addTask(newTask);

        assertEquals(newTask.getName(), added.getName());
        assertEquals(newTask.getDescription(), added.getDescription());
        assertEquals(newTask.getStatus(), added.getStatus());
    }

    @Test
    void checkAddTaskToHistory() {
        int prevHistorySize = manager.getHistory().size();
        int taskId = task.getId();
        Task viewed = manager.getTask(taskId).clone();
        List<Task> history = manager.getHistory();
        Task foundInHistory = history.getLast();

        assertEquals(prevHistorySize + 1, history.size());
        assertEquals(foundInHistory, viewed);

        // Убедимся, что в истории останется прежняя версия таски, если мы ее модифицируем через менеджер
        manager.updateTask(new Task(taskId, "checkAddTaskToHistory", "New desc", Status.DONE));
        assertEquals(manager.getHistory().getLast(), viewed);
    }


    @Test
    void checkUpdateTask() {
        Task updated = task.clone();

        String newName = "Редактированное имя";
        String newDescription = "Редактированное описание";
        Status newStatus = Status.DONE;

        updated.setName(newName);
        updated.setDescription(newDescription);
        updated.setStatus(newStatus);

        manager.updateTask(updated);
        Task found = manager.getTask(task.getId());

        assertNotNull(found);
        assertEquals(newName, found.getName());
        assertEquals(newDescription, found.getDescription());
        assertEquals(newStatus, found.getStatus());
    }

    @Test
    void checkUpdateEpic() {

        Epic updated = epic.clone();

        Status oldStatus = epic.getStatus();
        HashSet<Integer> oldSubtasks = epic.getSubtasks();

        String newName = "checkUpdateEpic";
        String newDescription = "description";

        updated.setName(newName);
        updated.setDescription(newDescription);
        updated.setStatus(Status.DONE); // статус вычисляется менеджером, это значение должно игнорироваться
        updated.addSubtask(22);      // список подзадач должен остаться прежним

        manager.updateEpic(updated);
        Epic found = manager.getEpic(epic.getId());

        assertNotNull(found);
        assertEquals(newName, found.getName());
        assertEquals(newDescription, found.getDescription());
        assertEquals(oldStatus, found.getStatus());
        assertEquals(oldSubtasks, found.getSubtasks());
    }

    @Test
    void checkUpdateSubtask() {

        Subtask updated = subtask.clone();

        String newName = "checkUpdateEpic";
        String newDescription = "description";
        Status newStatus = Status.IN_PROGRESS;
        int oldEpidId = subtask.getEpicId();

        updated.setName(newName);
        updated.setDescription(newDescription);
        updated.setStatus(newStatus);
        updated.setEpicId(22);          // id эпика должен остаться прежним

        manager.updateSubtask(updated);
        Subtask found = manager.getSubtask(subtask.getId());

        assertNotNull(found);
        assertEquals(newName, found.getName());
        assertEquals(newDescription, found.getDescription());
        assertEquals(newStatus, found.getStatus());
        assertEquals(oldEpidId, found.getEpicId());
    }

    @Test
    void checkEpicStatusIfNoSubtasks() {
        manager.removeAllSubtasks();
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void checkEpicStatusIfAllSubtasksAreNew() {
        manager.removeAllSubtasks();
        manager.addSubtask(new Subtask("Tmp1", "Desc", Status.NEW, epic.getId()));
        manager.addSubtask(new Subtask("Tmp2", "Desc", Status.NEW, epic.getId()));
        manager.addSubtask(new Subtask("Tmp3", "Desc", Status.NEW, epic.getId()));
        Status expected = Status.NEW;

        assertEquals(expected, epic.getStatus());
    }

    @Test
    void checkEpicStatusIfAllSubtasksAreDone() {
        manager.removeAllSubtasks();
        manager.addSubtask(new Subtask("Tmp1", "Desc", Status.DONE, epic.getId()));
        manager.addSubtask(new Subtask("Tmp2", "Desc", Status.DONE, epic.getId()));
        manager.addSubtask(new Subtask("Tmp3", "Desc", Status.DONE, epic.getId()));
        Status expected = Status.DONE;

        assertEquals(expected, epic.getStatus());
    }

    @Test
    void checkEpicStatusIfDifferentStatuses() {
        manager.removeAllSubtasks();
        manager.addSubtask(new Subtask("Tmp1", "Desc", Status.NEW, epic.getId()));
        manager.addSubtask(new Subtask("Tmp2", "Desc", Status.DONE, epic.getId()));
        manager.addSubtask(new Subtask("Tmp3", "Desc", Status.DONE, epic.getId()));
        Status expected = Status.IN_PROGRESS;

        assertEquals(expected, epic.getStatus());

        manager.addSubtask(new Subtask("Tmp3", "Desc", Status.IN_PROGRESS, epic.getId()));
        assertEquals(expected, epic.getStatus());
    }
}