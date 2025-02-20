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

            Task task1 = new Task("Выгулять собаку", "взять с собой пакетики", Status.NEW);
            task1.setStartTime(LocalDateTime.of(2025, 1, 10, 8, 0));
            task1.setDuration(Duration.ofMinutes(60));
            manager.addTask(task1);

            Task task2 = new Task("Покормить собаку", "и угостить запеканкой", Status.NEW);
            task2.setStartTime(LocalDateTime.of(2025, 1, 10, 9, 10));
            task2.setDuration(Duration.ofMinutes(60));
            manager.addTask(task2);

            Epic epic1 = manager.addEpic(new Epic("Убраться на столе", "на рабочем"));
            Epic epic2 = manager.addEpic(new Epic("Выполнить ФЗ спринта", "качественно"));

            Subtask su1 = new Subtask("Убрать лишние вещи", "по местам", Status.NEW, epic1.getId());
            Subtask su2 = new Subtask("Протереть пыль", "", Status.NEW, epic1.getId());
            Subtask su3 = new Subtask("Написать код", "не отвлекаться", Status.NEW, epic2.getId());

            su1.setStartTime(LocalDateTime.of(2025, 2, 18, 12, 0));
            su2.setStartTime(LocalDateTime.of(2025, 1, 10, 11, 0));
            su3.setStartTime(LocalDateTime.of(2025, 2, 11, 12, 0));

            su1.setDuration(Duration.ofHours(20));
            su2.setDuration(Duration.ofHours(3));
            su3.setDuration(Duration.ofMinutes(30));

            manager.addSubtask(su1);
            manager.addSubtask(su2);
            manager.addSubtask(su3);
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
