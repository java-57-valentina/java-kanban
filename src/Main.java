import exception.TaskTimeConflictException;
import taskmanager.Managers;
import taskmanager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getFileBackedTaskManager(Paths.get("tasks.csv"));

        if (manager.getTasks().isEmpty()) {
            System.out.println("Создание объектов ...");

            Task task1 = new Task("Выгулять собаку", "взять с собой пакетики", Status.NEW,
                    LocalDateTime.of(2025, 1, 10, 8, 0),
                    Duration.ofMinutes(60));
            try {
                manager.addTask(task1);
            } catch (TaskTimeConflictException e) {
                System.err.println(e.getMessage());
            }

            Task task2 = new Task("Покормить собаку", "и угостить запеканкой", Status.NEW,
                    LocalDateTime.of(2025, 1, 10, 9, 10),
                    Duration.ofMinutes(60));
            try {
                manager.addTask(task2);
            } catch (TaskTimeConflictException e) {
                System.err.println(e.getMessage());
            }

            Epic epic1 = manager.addEpic(new Epic("Убраться на столе", "на рабочем"));
            Epic epic2 = manager.addEpic(new Epic("Выполнить ФЗ спринта", "качественно"));

            Subtask su1 = new Subtask("Убрать лишние вещи", "по местам", Status.NEW, epic1.getId(),
                    LocalDateTime.of(2025, 2, 18, 12, 0),
                    Duration.ofMinutes(20));
            Subtask su2 = new Subtask("Протереть пыль", "", Status.NEW, epic1.getId(),
                    LocalDateTime.of(2025, 1, 10, 11, 0),
                    Duration.ofMinutes(20));
            Subtask su3 = new Subtask("Написать код", "не отвлекаться", Status.NEW, epic2.getId(),
                    LocalDateTime.of(2025, 2, 11, 12, 0),
                    Duration.ofMinutes(20));

            try {
                manager.addSubtask(su1);
                manager.addSubtask(su2);
                manager.addSubtask(su3);
            } catch (TaskTimeConflictException e) {
                System.err.println(e.getMessage());
            }
        }

        Task task2 = new Task("Важный звонок", "", Status.NEW,
                LocalDateTime.of(2025, 1, 10, 9, 30),
                Duration.ofMinutes(30));
        try {
            manager.addTask(task2);
        } catch (TaskTimeConflictException e) {
            System.err.println(e.getMessage());
        }

        printTasks(manager.getTasks(), "Задачи", manager);
        printTasks(manager.getEpics(), "Эпики", manager);
        printTasks(manager.getSubtasks(), "Подзадачи", manager);
        printTasks(manager.getPrioritizedTasks(), "Список задач по приоритету", manager);
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
