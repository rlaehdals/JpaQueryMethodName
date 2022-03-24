package com.example.blogjpa.jpa;

import com.example.blogjpa.member.domain.Member;
import com.example.blogjpa.member.domain.Team;
import com.example.blogjpa.member.repository.MemberRepository;
import com.example.blogjpa.member.repository.TeamRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class FetchJoinTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;


    @Autowired
    EntityManager em;

    @Test
    @DisplayName("멤버가 팀을 생성")
    void memberCreateTeam(){

        /*
        given
         */
        Member member = Member.builder().age(10).name("m1").build();
        Member saveMember = memberRepository.save(member);

        /*
        when
         */
        Team team = Team.builder().name("team1").member(saveMember).build();
        Team saveTeam = teamRepository.save(team);

        /*
        then
         */
        assertThat(saveTeam.getName()).isEqualTo("team1");
        assertThat(saveTeam.getMembers().size()).isEqualTo(1);
        assertThat(saveTeam.getMembers().get(0).getName()).isEqualTo("m1");
    }

    @Test
    @DisplayName("팀에 새로운 멤버 추가")
    void teamAddMember(){
        /*
        given
         */
        Member member = Member.builder().age(10).name("m1").build();
        Member member2 = Member.builder().age(10).name("m2").build();
        Member saveMember = memberRepository.save(member);
        Member saveMember2 = memberRepository.save(member2);
        Team team = Team.builder().name("team1").member(saveMember).build();
        Team saveTeam = teamRepository.save(team);

        /*
        when
         */
        saveTeam.addMember(member2);
        Team result = teamRepository.findByName("team1").get();

        /*
        then
         */
        assertThat(result.getMembers().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("페치조인 사용하지 않고, 팀의 이름을 조회할 때")
    @Transactional
    void notFetchJoinMemberFindTeamName(){

        Member member = Member.builder().age(10).name("m1").build();
        Member saveMember = memberRepository.save(member);

        Team team = Team.builder().name("team1").member(saveMember).build();
        Team saveTeam = teamRepository.save(team);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findNotFetchJoinByName("m1").get();

        Team memberTeam = findMember.getTeam();

        assertThat(memberTeam.getName()).isEqualTo("team1");
    }

    @Test
    @DisplayName("페치조인 사용하고, 팀의 이름을 조회할 때")
    @Transactional
    void FetchJoinMemberFindTeamName(){
        Member member = Member.builder().age(10).name("m1").build();
        Member saveMember = memberRepository.save(member);

        Team team = Team.builder().name("team1").member(saveMember).build();
        Team saveTeam = teamRepository.save(team);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findFetchJoinByName("m1").get();

        Team memberTeam = findMember.getTeam();

        assertThat(memberTeam.getName()).isEqualTo("team1");
    }

    @Test
    @DisplayName("엔티티 그래프 페치 조인 사용")
    @Transactional
    void EntityGraphFetchJoin(){
        Member member = Member.builder().age(10).name("m1").build();
        Member saveMember = memberRepository.save(member);

        Team team = Team.builder().name("team1").member(saveMember).build();
        Team saveTeam = teamRepository.save(team);

        em.flush();
        em.clear();

        Member findMember = memberRepository.findEntityGraphFetchJoinByName("m1").get();

        Team memberTeam = findMember.getTeam();

        assertThat(memberTeam.getName()).isEqualTo("team1");
    }
}
