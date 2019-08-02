package com.oristar.vo;

public class Person {
    private int age;
    private int sex;
    private String des;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", sex=" + sex +
                ", des='" + des + '\'' +
                '}';
    }

    public Person(int age, int sex) {
        this.age = age;
        this.sex = sex;
    }

    public Person() {
    }

    public String sayHi(int age) {
        return "我 " + age + "岁啦！";
    }
}
