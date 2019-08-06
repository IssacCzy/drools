package com.oristar.vo;

/**
 * @Author Issac
 * @Description TODO
 * @Date 2019/8/5 15:50
 * @Version 1.0
 **/
public class Student {
    private String name;
    private int age;
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Student() {
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", desc='" + desc + '\'' +
                '}';
    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
