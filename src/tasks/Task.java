package tasks;

import exception.LoadTaskException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

public class Task implements Cloneable {

    protected int id;
    protected String name;
    protected String description;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    static DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    public Task(int id, String name, String description, Status status, LocalDateTime time, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = time;
        this.duration = duration;
    }

    public Task(String name, String description, Status status, LocalDateTime time, Duration duration) {
        this(0, name, description, status, time, duration);
    }

    public Task(String name, String description, Status status) {
        this(name, description, status, null, null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null)
            return null;
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return String.format("TASK: %d, %s, %s, %s, %s, %s min\n", id, name, description, status.toString(),
                startTime == null ? "null" : startTime,
                duration == null ? "null" : duration.toMinutes());
    }

    public String toLine() {
        return String.format("TASK, %d, %s, %s, %s, %s, %s\n", id, name, description, status.toString(),
                startTime == null ? "null" : startTime.format(formatter),
                duration == null ? "null" : duration.toMinutes());
    }

    public static Task fromLine(List<String> parts) throws LoadTaskException {
        if (parts.size() < 6)
            throw new LoadTaskException("Неверный формат строки");

        try {
            int id = Integer.parseInt(parts.get(0));
            String name = parts.get(1);
            String desk = parts.get(2);
            Status status = Status.valueOf(parts.get(3));
            Task task = new Task(id, name, desk, status, null, null);
            try {
                LocalDateTime startTime = LocalDateTime.parse(parts.get(4), formatter);
                task.setStartTime(startTime);
            }
            catch (DateTimeParseException ignored) {
                
            }
            try {
                Duration duration = Duration.ofMinutes(Integer.parseInt(parts.get(5)));
                task.setDuration(duration);
            } catch (NumberFormatException ignored) { }
            return task;

        } catch (IllegalArgumentException e) {
            throw new LoadTaskException("Неподдерживаемый формат строки: " + String.join(",", parts));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Task task = (Task) o;
        return id == task.id
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && Objects.equals(startTime, task.startTime)
                && Objects.equals(duration, task.duration)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, startTime, duration);
    }

    @Override
    public Task clone() {
        try {
            Task clone = (Task) super.clone();
            clone.setId(this.id);
            clone.setName(this.name);
            clone.setDescription(this.description);
            clone.setStatus(this.status);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
