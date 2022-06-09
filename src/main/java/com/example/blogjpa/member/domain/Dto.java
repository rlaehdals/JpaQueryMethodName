package com.example.blogjpa.member.domain;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Dto {

    public String username;
    public int age;


    @QueryProjection
    public Dto(String username, int age){
        this.username=username;
        this.age=age;
    }
}
