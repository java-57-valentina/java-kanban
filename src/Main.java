import taskmanager.Managers;
import taskmanager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getFileBackedTaskManager(Paths.get("tasks.csv"));

        if (manager.getTasks().isEmpty()) {
            System.out.println("Создание объектов ...");

            manager.addTask(new Task("Выгулять собаку", "взять с собой пакетики", Status.NEW));
            manager.addTask(new Task("Покормить собаку", "и угостить запеканкой", Status.NEW));
            manager.addTask(new Task("Купить подарки друзьям", "", Status.NEW));

            Epic epic1 = manager.addEpic(new Epic("Убраться на столе", "на рабочем"));
            Epic epic2 = manager.addEpic(new Epic("Выполнить ФЗ спринта", "качественно"));

            manager.addSubtask(new Subtask("Убрать лишние вещи", "по местам", Status.NEW, epic1.getId()));
            manager.addSubtask(new Subtask("Протереть пыль", "", Status.NEW, epic1.getId()));
            manager.addSubtask(new Subtask("Написать код", "не отвлекаться", Status.NEW, epic2.getId()));
        }

        printTasks(manager.getTasks(), "Задачи", manager);
        printTasks(manager.getEpics(), "Эпики", manager);
        printTasks(manager.getSubtasks(), "Подзадачи", manager);
    }

    private static void printTasks(List<? extends Task> list, String title, TaskManager manager) {
        System.out.println();
        System.out.println(title + ":");

        if (list.isEmpty()) {
            System.out.println("[список пуст]");
            return;
        }

        for (Task task : list) {
            System.out.print("• " + task);
            if (task instanceof Epic) {
                for (Task subtask : manager.getSubtasksByEpicId(task.getId())) {
                    System.out.print("   → " + subtask);
                }
            }
        }
    }
}
