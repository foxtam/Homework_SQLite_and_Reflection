package net.foxtam.lesson9_homework;

import net.foxtam.lesson9_homework.annotations.Column;
import net.foxtam.lesson9_homework.annotations.Table;

@Table(title = "cats")
public class Cat {
    @Column
    private final String name;

    @Column
    private final int age;

    public Cat(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
