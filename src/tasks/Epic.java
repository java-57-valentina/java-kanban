package tasks;

import exception.LoadTaskException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private HashSet<Integer> subtasks;
    private LocalDateTime endTime;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW, null, null);
        subtasks = new HashSet<>();
    }

    public Epic(String name, String description) {
        this(0, name, description);
    }

    public HashSet<Integer> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(int id) {
        subtasks.remove(id);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public boolean addSubtask(Integer id) {
        return subtasks.add(id);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks)
                && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks, endTime);
    }

    @Override
    public String toString() {

        return String.format("EPIC: %d, %s, %s, %s, %s, %s, %s min\n",
                id,
                name,
                description,
                status.toString(),
                startTime == null ? "null" : startTime.format(formatter),
                endTime == null ? "null" : endTime.format(formatter),
                duration == null ? "null" : duration.toMinutes());
    }

    @Override
    public String toLine() {
        return String.format("EPIC, %d, %s, %s\n",
                id,
                name,
                description);
    }

    public static Epic fromLine(List<String> parts) throws LoadTaskException {
        if (parts.size() < 3)
            throw new LoadTaskException("Неверный формат строки");

        try {
            int id = Integer.parseInt(parts.get(0));
            String name = parts.get(1);
            String desk = parts.get(2);
            return new Epic(id, name, desk);
        } catch (IllegalArgumentException e) {
            throw new LoadTaskException("Неподдерживаемый формат строки: " + String.join(",", parts));
        }
    }

    @Override
    public Epic clone() {
        Epic clone = (Epic) super.clone();
        clone.subtasks = new HashSet<>(this.subtasks);
        return clone;
    }
}
