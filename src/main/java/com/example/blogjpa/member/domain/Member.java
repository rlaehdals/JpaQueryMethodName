package com.example.blogjpa.member.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = {"team"})
public class Member {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",unique = true)
    private String name;

    @Column(name = "age")
    private int age;

    @Builder
    public Member(String name, int age){
        this.name=name;
        this.age=age;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public void changeAge(int age){
        this.age=age;
    }

    public void addTeam(Team team){
        team.getMembers().add(this);
        this.team=team;
    }
}
