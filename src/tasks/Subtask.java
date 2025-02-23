package tasks;

import exception.LoadTaskException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String name, String description, Status status, int epicId, LocalDateTime time,
                   Duration duration) {
        super(id, name, description, status, time, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId, LocalDateTime time,
                   Duration duration) {
        this(0, name, description, status, epicId, time, duration);
    }

    public Subtask(String name, String description, Status status, int epicId) {
        this(name, description, status, epicId, null, null);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return String.format("SUBTASK: %d, %s, %s, %s, %d, %s, %s min\n", id, name, description, status.toString(), epicId,
                startTime != null ? startTime.format(formatter) : "-",
                duration != null ? "" + duration.toMinutes() : "-");
    }

    @Override
    public String toLine() {
        return String.format("SUBTASK, %d, %s, %s, %s, %d, %s, %s\n", id, name, description, status.toString(), epicId,
                startTime == null ? "null" : startTime,
                duration == null ? "null" : duration.toMinutes());
    }

    public static Subtask fromLine(List<String> parts) throws LoadTaskException {
        if (parts.size() < 7)
            throw new LoadTaskException("Неверный формат строки");

        try {
            int id = Integer.parseInt(parts.get(0));
            int epicId = Integer.parseInt(parts.get(4));
            String name = parts.get(1);
            String desk = parts.get(2);
            Status status = Status.valueOf(parts.get(3));
            Subtask subtask = new Subtask(id, name, desk, status, epicId, null, null);
            try {
                LocalDateTime startTime = LocalDateTime.parse(parts.get(5), formatter);
                subtask.setStartTime(startTime);
            } catch (DateTimeParseException ignored) { }
            try {
                Duration duration = Duration.ofMinutes(Integer.parseInt(parts.get(6)));
                subtask.setDuration(duration);
            } catch (NumberFormatException ignored) { }
            return subtask;
        } catch (IllegalArgumentException e) {
            throw new LoadTaskException("Неподдерживаемый формат строки: " + String.join(",", parts));
        }
    }

    @Override
    public Subtask clone() {
        Subtask clone = (Subtask) super.clone();
        clone.setEpicId(this.epicId);
        return clone;
    }
}
