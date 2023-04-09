package org.itstep;

public enum Priority {
    HIGH(1, "High"),
    NORMAL(2, "Normal"),
    LOW(3, "Low");

    private final int num;
    private final String priority;

    Priority(int num, String priority) {    // Конструктор
        this.num = num;
        this.priority = priority;
    }

    int num() {
        return num;
    }

    String priority() {
        return priority;
    }

    @Override
    public String toString() {
        return String.format("%d: %s", num, priority);
    }
}

