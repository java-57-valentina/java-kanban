package tasks;

import java.util.Objects;

public class Task implements Cloneable {

    protected int id;
    protected String name;
    protected String description;
    protected Status status;


    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Status status) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.status = status;
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

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
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
