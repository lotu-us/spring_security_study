package com.example.demo.domain;

import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  //protected 빈 생성자를 만들어준다
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer age;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn     //team_id로 자동으로 설정됨
    private Team team;

    @Builder
    public Member(String name, Integer age, Team team) {
        this.name = name;
        this.age = age;
        this.team = team;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", team=" + team +
                '}';
    }
}
