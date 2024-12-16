import taskmanager.InMemoryTaskManager;
import taskmanager.TaskManager;
import tasks.*;

import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();

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

        printTasks(manager.getTasks(), "Задачи");
        printTasks(manager.getEpics(), "Эпики");
        printTasks(manager.getSubtasks(), "Подзадачи");

        System.out.println("\nИзменение статусов задач ...");
        /* Создаем клоны существующих объектов и модифицируем их
        иначе методы менеджера теряют смысл */
        task1 = new Task(task1);
        task2 = new Task(task2);
        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);

        manager.updateTask(task1);
        manager.updateTask(task2);
        printTasks(manager.getTasks(), "Задачи");

        System.out.println("\nИзменение статусов эпиков ...");
        epic1 = new Epic(epic1);
        epic2 = new Epic(epic2);
        epic1.setStatus(Status.IN_PROGRESS);
        epic2.setStatus(Status.DONE);

        // expected behavior: the statuses will not be changed
        manager.updateEpic(epic1);
        manager.updateEpic(epic2);
        printTasks(manager.getEpics(), "Эпики");

        System.out.println("\nИзменение статусов подзадач ...");
        subtask1 = new Subtask(subtask1);
        subtask2 = new Subtask(subtask2);
        subtask3 = new Subtask(subtask3);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);

        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        manager.updateSubtask(subtask3);
        printTasks(manager.getEpics(), "Эпики");
        printTasks(manager.getSubtasks(), "Подзадачи");

        System.out.println("\nУдаление задачи id:" + task1.getId() + " ...");
        manager.removeTask(task1.getId());
        printTasks(manager.getTasks(), "Задачи");

        System.out.println("\nУдаление эпика id:" + epic1.getId() + " ...");
        manager.removeEpic(epic1.getId());
        printTasks(manager.getEpics(), "Эпики");
        printTasks(manager.getSubtasks(), "Подзадачи");

        System.out.println("\nДобавление подзадачи ...");
        Subtask subtask4 = new Subtask("Закоммитить изменения", "в гитхаб", Status.NEW, epic2.getId());
        subtask4 = manager.addSubtask(subtask4);
        printTasks(manager.getEpics(), "Эпики");
        printTasks(manager.getSubtasks(), "Подзадачи");

        System.out.println("\nУдаление всех эпиков ...");
        manager.removeAllEpics();
        printTasks(manager.getEpics(), "Эпики");
        printTasks(manager.getSubtasks(), "Подзадачи");

        System.out.println("\nОтображение истории просмотра ...");
        printTasks(manager.getHistory(), "История");

        System.out.println("\nПросмотр всех задач ...");
        for (Task task : manager.getTasks()) {
            System.out.println(manager.getTask(task.getId()));
        }

        System.out.println("\nОтображение истории просмотра ...");
        printTasks(manager.getHistory(), "История");

        System.out.println("\nУдаление рандомной таски ...");
        Random random = new Random();
        int idx = random.nextInt(manager.getTasks().size());
        Task rndTask = manager.getTasks().get(idx);
        System.out.println(rndTask);
        manager.removeTask(rndTask.getId());

        System.out.println("\nПовторное отображение истории просмотра ...");
        printTasks(manager.getHistory(), "История");
    }

    private static void printTasks(List<? extends Task> list, String title) {
        System.out.println();
        System.out.println(title + ":");
        for (Task task : list)
            System.out.println(task);
    }
}
