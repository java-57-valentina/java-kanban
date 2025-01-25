import taskmanager.Managers;
import taskmanager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        System.out.println("Создание объектов ...");
        Task task1 = new Task("Выгулять собаку", "взять с собой лакомства и пакетики", Status.NEW);
        Task task2 = new Task("Покормить собаку", "и угостить запеканкой", Status.NEW);
        task1 = manager.addTask(task1);
        task2 = manager.addTask(task2);

        manager.addTask(new Task("Купить подарки друзьям", "", Status.NEW));

        Epic epic1 = new Epic("Убраться на столе", "на рабочем");
        Epic epic2 = new Epic("Выполнить проект №4", "качественно");
        epic1 = manager.addEpic(epic1);
        epic2 = manager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Убрать лишние вещи", "по местам", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Протереть пыль", "", Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Написать код", "не отвлекаться", Status.NEW, epic2.getId());
        subtask1 = manager.addSubtask(subtask1);
        subtask2 = manager.addSubtask(subtask2);
        subtask3 = manager.addSubtask(subtask3);

        printTasks(manager.getTasks(), "Задачи", manager);
        printTasks(manager.getEpics(), "Эпики", manager);
        printTasks(manager.getSubtasks(), "Подзадачи", manager);

        System.out.println("\nИзменение статусов задач ...");
        /* Создаем клоны существующих объектов и модифицируем их
        иначе методы менеджера теряют смысл */
        task1 = task1.clone();
        task2 = task2.clone();
        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);

        manager.updateTask(task1);
        manager.updateTask(task2);
        printTasks(manager.getTasks(), "Задачи", manager);

        System.out.println("\nИзменение статусов эпиков ...");
        epic1 = epic1.clone();
        epic2 = epic2.clone();
        epic1.setStatus(Status.IN_PROGRESS);
        epic2.setStatus(Status.DONE);

        // expected behavior: the statuses will not be changed
        manager.updateEpic(epic1);
        manager.updateEpic(epic2);
        printTasks(manager.getEpics(), "Эпики", manager);

        System.out.println("\nИзменение статусов подзадач ...");
        subtask1 = subtask1.clone();
        subtask2 = subtask2.clone();
        subtask3 = subtask3.clone();
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);

        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        manager.updateSubtask(subtask3);
        printTasks(manager.getEpics(), "Эпики", manager);
        printTasks(manager.getSubtasks(), "Подзадачи", manager);

        System.out.println("\nУдаление задачи id:" + task1.getId() + " ...");
        manager.removeTask(task1.getId());
        printTasks(manager.getTasks(), "Задачи", manager);

        System.out.println("\nУдаление эпика id:" + epic1.getId() + " ...");
        manager.removeEpic(epic1.getId());
        printTasks(manager.getEpics(), "Эпики", manager);
        printTasks(manager.getSubtasks(), "Подзадачи", manager);

        System.out.println("\nДобавление подзадачи ...");
        Subtask subtask4 = new Subtask("Закоммитить изменения", "в гитхаб", Status.NEW, epic2.getId());
        subtask4 = manager.addSubtask(subtask4);
        printTasks(manager.getEpics(), "Эпики", manager);
        printTasks(manager.getSubtasks(), "Подзадачи", manager);

        System.out.println("\nУдаление всех эпиков ...");
        manager.removeAllEpics();
        printTasks(manager.getEpics(), "Эпики", manager);
        printTasks(manager.getSubtasks(), "Подзадачи", manager);

        System.out.println("\nОтображение истории просмотра ...");
        printTasks(manager.getHistory(), "История", manager);

        System.out.println("\nПросмотр и редактирование таски ...");
        int taskId = manager.getTasks().getLast().getId();
        Task taskForUpdate = manager.getTask(taskId).clone();
        System.out.println(taskForUpdate);
        taskForUpdate.setStatus(Status.IN_PROGRESS);
        manager.updateTask(taskForUpdate);
        Task updated = manager.getTask(taskId);
        System.out.println(updated);

        System.out.println("\nОтображение истории просмотра ...");
        printTasks(manager.getHistory(), "История", manager);

        System.out.println("\nДополнительное задание");
        System.out.println("\nУдаление всех задач, подзадач, эпиков ...");

        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();

        printTasks(manager.getHistory(), "История", manager);

        System.out.println("\nСоздание и просмотр задач/эпиков в разном порядке ...");

        task1 = manager.addTask(
                new Task("Купить швейную машинку", "Brother", Status.NEW));
        task2 = manager.addTask(
                new Task("Купить ткань", "в клетку", Status.NEW));

        epic1 = manager.addEpic(
                new Epic("Сшить пиджак", "по выкройке Бурда 2020/4"));
        epic2 = manager.addEpic(
                new Epic("Почистить швейную машинку", "согл. инструкции"));

        subtask1 = manager.addSubtask(
                new Subtask("Раскроить", "7 раз отмерить", Status.NEW, epic1.getId()));
        subtask2 = manager.addSubtask(
                new Subtask("Продублировать", "дублерином", Status.NEW, epic1.getId()));
        subtask3 = manager.addSubtask(
                new Subtask("Собрать", "Без слез", Status.NEW, epic1.getId()));

        manager.getTask(task2.getId());
        manager.getEpic(epic2.getId());
        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask3.getId());

        printTasks(manager.getHistory(), "История", manager);

        System.out.println("\nОбновление задачи '" + task2.getName() + "' и повторный просмотр ...");

        task2.setStatus(Status.DONE);
        manager.updateTask(task2);
        manager.getTask(task2.getId());

        printTasks(manager.getHistory(), "История", manager);

        System.out.println("\nУдаление задачи '" + task2.getName() + "' ...");
        manager.removeTask(task2.getId());
        printTasks(manager.getHistory(), "История", manager);

        System.out.println("\nУдаление эпика '" + epic1.getName() + "' ...");
        manager.removeEpic(epic1.getId());
        printTasks(manager.getHistory(), "История", manager);
    }

    private static void printTasks(List<? extends Task> list, String title, TaskManager manager) {
        System.out.println();
        System.out.println(title + ":");

        if (list.isEmpty()) {
            System.out.println("[список пуст]");
            return;
        }

        for (Task task : list) {
            System.out.println("• " + task);
            if (task instanceof Epic) {
                for (Task subtask : manager.getSubtasksByEpicId(task.getId())) {
                    System.out.println("   → " + subtask);
                }
            }
        }
    }
}
