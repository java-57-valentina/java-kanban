package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId) {
        this(0, name, description, status, epicId);
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
        return "Subtask{" +
                "id=" + id +
                ", epicId=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public Subtask clone() {
        Subtask clone = (Subtask) super.clone();
        clone.setEpicId(this.epicId);
        return clone;
    }
}
