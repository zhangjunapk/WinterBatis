package org.zj.winterbatis.bean;

import org.zj.winterbatis.core.annotation.Id;

/**
 * Created by ZhangJun on 2018/9/7.
 */
public class Teacher {
    @Id
    private Integer id;
    private String username;
    private Integer age;

    public Teacher() {
    }

    public Teacher(Integer id, String username, Integer age) {
        this.id = id;
        this.username = username;
        this.age = age;
    }

    public Teacher(String username, Integer age) {
        this.username = username;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
