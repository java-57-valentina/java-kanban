package tasks;

import java.util.HashSet;

public class Epic extends Task {
    private final HashSet<Integer> subtasks;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        subtasks = new HashSet<>();
    }

    public Epic(String name, String description, Status status) {
        this(0, name, description,status);
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
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                // ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasks=" + subtasks +
                '}';
    }



}
