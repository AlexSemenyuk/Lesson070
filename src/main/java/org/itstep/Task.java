package org.itstep;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Task implements Serializable, Externalizable, Comparable {
    private static int count;
    private static SimpleDateFormat dateTemplate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private int id;
    private String name;
    private String description;
    private Category category;
    private Date deadline;
    private Priority priority;
    private Condition condition;

    public Task() {
    }

    public Task(String name, String description, Category category, String data, Priority priority) throws ParseException {
        this.id = ++count;
        this.name = name;
        this.description = description;
        this.category = category;
        this.deadline = dateTemplate.parse(data);
        this.priority = priority;
        this.condition = Condition.ACTIVE;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "Task{" +
               "id='" + id + '\'' +
               "name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", category=" + category +
               ", deadline='" + dateTemplate.format(deadline) + '\'' +
               ", priority=" + priority +
               ", condition=" + condition +
               '}';
    }


    public String toStringForHome() {
        return "<div class='task'>" +
               "<span class='itemname'>" + name + "</span>" +
               "<span class='itemdescript'>" + description + "</span>" +
               "<span class='itemcategogy'>" + category.category() + "</span>" +
               "<span class='itemdeadline'>" + dateTemplate.format(deadline) + "</span>" +
               "<span class='itempriority'>" + priority.priority() + "</span>" +
               "<a class='button-cond' role='button' href='?condition=" + id + "'>Done</a>" +
               "<a class='button-delete' role='button' href='?delete=" + id + "'>Delete</a>" +
               "</div>";
    }

    public String toStringForDone() {
        return "<div class='task'>" +
               "<span class='itemname'>" + name + "</span>" +
               "<span class='itemdescript'>" + description + "</span>" +
               "<span class='itemcategogy'>" + category.category() + "</span>" +
               "<span class='itemdeadline'>" + dateTemplate.format(deadline) + "</span>" +
               "<span class='itempriority'>" + priority.priority() + "</span>" +
               "<a class='button-cond' role='button' href='?condition=" + id + "'>Active</a>" +
               "<a class='button-delete' role='button' href='?delete=" + id + "'>Delete</a>" +
               "</div>";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Task) {
            if (((Task) o).getName().equals(name) && ((Task) o).getDescription().equals(description) &&
                ((Task) o).getDeadline().equals(deadline) && ((Task) o).getPriority().equals(priority)) {
                return true;
            }
        }
//        if (o == null || getClass() != o.getClass()) return false;
//        Task task = (Task) o;
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return priority.num() - ((Task) o).getPriority().num();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(id);
        out.writeObject(name);
        out.writeObject(description);
        out.writeObject(category);
        out.writeObject(deadline);
        out.writeObject(priority);
        out.writeObject(condition);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = (int) in.readObject();
        name = (String) in.readObject();
        description = (String) in.readObject();
        category = (Category) in.readObject();
        deadline = (Date) in.readObject();
        priority = (Priority) in.readObject();
        condition = (Condition) in.readObject();
    }
}
