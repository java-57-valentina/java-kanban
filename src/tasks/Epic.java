package tasks;

import java.util.HashSet;
import java.util.Objects;

public class Epic extends Task {
    private HashSet<Integer> subtasks;

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
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
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        return String.format("EPIC, %d, %s, %s, %s\n", id, name, description, status.toString());
    }

    @Override
    public Epic clone() {
        Epic clone = (Epic) super.clone();
        clone.subtasks = new HashSet<>(this.subtasks);
        return clone;
    }
}
