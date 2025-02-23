package exception;

public class TaskTimeConflictException extends RuntimeException {
    public TaskTimeConflictException(String name, Integer otherId) {
        super("Задача '" + name + "' пересекается по времени с задачей name:" + otherId);
    }
}
