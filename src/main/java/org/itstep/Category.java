package org.itstep;

public enum Category {
    HOUSE(1, "House"),
    WORK(2, "Work"),
    FITNESS(3, "Fitness"),
    SHOPPING(4, "Shopping");

    private final int num;
    private final String category;

    Category(int num, String category) {    // Конструктор
        this.num = num;
        this.category = category;
    }

    int num() {
        return num;
    }

    String category() {
        return category;
    }

    @Override
    public String toString() {
        return String.format("%d: %s", num, category);
    }
}




