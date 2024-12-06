package tasks;

import java.util.HashSet;
import java.util.Objects;

public class Epic extends Task {
    private final HashSet<Integer> subtasks;

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
        subtasks = new HashSet<>();
    }

    public Epic(String name, String description) {
        this(0, name, description);
    }

    public Epic(Epic prototype) {
        super(prototype);
        this.subtasks = new HashSet<>();
        subtasks.addAll(prototype.subtasks);
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
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasks=" + subtasks +
                '}';
    }
}
