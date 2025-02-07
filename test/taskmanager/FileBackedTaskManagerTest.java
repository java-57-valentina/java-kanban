package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private Task task;
    private Epic epic;
    private Subtask subtask;
    private TaskManager manager;
    private Path tmpPath = null;

    @BeforeEach
    void initManager() {
        try {
            tmpPath = Files.createTempFile("tmp", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        manager = Managers.getFileBackedTaskManager(tmpPath);

        task = manager.addTask(new Task("Задача1", "Описание задачи", Status.NEW));
        epic = manager.addEpic(new Epic("Эпик1", "Описание эпика"));
        subtask = manager.addSubtask(
                new Subtask("Подзадача1", "Описание подзадачи", Status.IN_PROGRESS, epic.getId()));
    }

    @Test
    void addTask() {
        Task added = manager.addTask(new Task("New task", "Desc", Status.DONE));
        TaskManager otherManager = Managers.getFileBackedTaskManager(tmpPath);

        assertEquals(2, manager.getTasks().size());
        assertEquals(2, otherManager.getTasks().size());
        assertNotNull(otherManager.getTask(added.getId()));
        assertEquals(manager.getTask(added.getId()), otherManager.getTask(added.getId()));
    }

    @Test
    void addEpic() {
        Task added = manager.addEpic(new Epic("New Epic", "Desc"));
        TaskManager otherManager = Managers.getFileBackedTaskManager(tmpPath);

        assertEquals(2, otherManager.getEpics().size());
        assertNotNull(otherManager.getEpic(added.getId()));
        assertEquals(manager.getEpic(added.getId()), otherManager.getEpic(added.getId()));
        assertEquals(Status.NEW, added.getStatus());
    }

    @Test
    void addSubtask() {
        Task added = manager.addSubtask(
                new Subtask("New Subtask", "Desc", Status.IN_PROGRESS, epic.getId()));
        TaskManager otherManager = Managers.getFileBackedTaskManager(tmpPath);

        Epic foundEpic = otherManager.getEpic(epic.getId());

        assertEquals(2, otherManager.getSubtasks().size());

        assertNotNull(otherManager.getSubtask(added.getId()));
        assertEquals(manager.getSubtask(added.getId()), otherManager.getSubtask(added.getId()));
        assertEquals(2, foundEpic.getSubtasks().size());
        assertEquals(Status.IN_PROGRESS, foundEpic.getStatus());
    }

    @Test
    void removeAllTasks() {
        manager.removeAllTasks();
        TaskManager otherManager = Managers.getFileBackedTaskManager(tmpPath);

        assertNull(manager.getTask(task.getId()));
        assertNull(otherManager.getTask(task.getId()));
        assertEquals(0, manager.getTasks().size());
        assertEquals(0, otherManager.getTasks().size());
    }

    @Test
    void removeAllEpics() {
        manager.removeAllEpics();
        TaskManager otherManager = Managers.getFileBackedTaskManager(tmpPath);

        assertNull(otherManager.getEpic(epic.getId()));
        assertNull(otherManager.getSubtask(subtask.getId()));
        assertEquals(0, otherManager.getEpics().size());
        assertEquals(0, otherManager.getSubtasks().size());
    }

    @Test
    void removeAllSubtasks() {
        manager.removeAllSubtasks();
        TaskManager otherManager = Managers.getFileBackedTaskManager(tmpPath);

        assertNull(otherManager.getSubtask(subtask.getId()));
        assertNotNull(otherManager.getEpic(epic.getId()));
        assertEquals(Status.NEW, otherManager.getEpic(epic.getId()).getStatus());
        assertEquals(0, otherManager.getEpic(epic.getId()).getSubtasks().size());
        assertEquals(0, otherManager.getSubtasks().size());
    }

    @Test
    void removeTask() {
        manager.removeTask(task.getId());
        TaskManager otherManager = Managers.getFileBackedTaskManager(tmpPath);

        assertEquals(0, otherManager.getTasks().size());
        assertNull(otherManager.getTask(task.getId()));
    }

    @Test
    void removeEpic() {
        manager.removeEpic(epic.getId());
        TaskManager otherManager = Managers.getFileBackedTaskManager(tmpPath);

        assertEquals(0, otherManager.getEpics().size());
        assertEquals(0, otherManager.getSubtasksByEpicId(epic.getId()).size());
        assertNull(otherManager.getEpic(epic.getId()));
    }

    @Test
    void removeSubtask() {
        manager.removeSubtask(subtask.getId());
        TaskManager otherManager = Managers.getFileBackedTaskManager(tmpPath);

        assertEquals(0, manager.getSubtasks().size());
        assertEquals(0, otherManager.getSubtasks().size());
        assertNull(otherManager.getSubtask(subtask.getId()));
        assertEquals(Status.NEW, otherManager.getEpic(epic.getId()).getStatus());
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
        manager.updateTask(updated);

        TaskManager otherManager = Managers.getFileBackedTaskManager(tmpPath);
        Task found = otherManager.getTask(task.getId());

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
        TaskManager otherManager = Managers.getFileBackedTaskManager(tmpPath);

        Epic found = otherManager.getEpic(epic.getId());

        assertNotNull(found);
        assertEquals(newName, found.getName());
        assertEquals(newDescription, found.getDescription());
        assertEquals(oldStatus, found.getStatus());
        assertEquals(oldSubtasks, found.getSubtasks());  // список подзадач должен остаться прежним
    }

    @Test
    void checkUpdateSubtask() {
        final String newName = "checkUpdateSubtask";
        final String newDescription = "description";
        final Status newStatus = Status.IN_PROGRESS;
        final int oldEpicId = subtask.getEpicId();

        Subtask updated = subtask.clone();
        updated.setName(newName);
        updated.setDescription(newDescription);
        updated.setStatus(newStatus);
        updated.setEpicId(22);

        manager.updateSubtask(updated);
        TaskManager otherManager = Managers.getFileBackedTaskManager(tmpPath);
        Subtask found = otherManager.getSubtask(subtask.getId());

        assertNotNull(found);
        assertEquals(newName, found.getName());
        assertEquals(newDescription, found.getDescription());
        assertEquals(newStatus, found.getStatus());
        assertEquals(oldEpicId, found.getEpicId());    // id эпика должен остаться прежним
    }
}