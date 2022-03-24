package com.example.blogjpa.member.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.print.attribute.standard.MediaSize;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Team {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(name = "team_name")
    private String name;


    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();


    @Builder
    public Team(String name, Member member){
        this.name=name;
        member.addTeam(this);
    }

    public void addMember(Member member){
        member.addTeam(this);
    }
}
