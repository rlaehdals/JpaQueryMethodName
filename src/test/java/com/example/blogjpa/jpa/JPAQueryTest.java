package com.example.blogjpa.jpa;

import com.example.blogjpa.member.domain.Member;
import com.example.blogjpa.member.repository.MemberRepository;
import com.example.blogjpa.member.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class JPAQueryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;



    @Test
    @DisplayName("단일 멤버 찾기 Id")
    void findById(){
        List<Member> members = createMember();

        Long id=0l;
        for(Member member: members){
            id = memberRepository.save(member).getId();
        }

        Member member = memberRepository.findById(id).get();

        assertThat(member.getName()).isEqualTo("m4");

    }

    @Test
    @DisplayName("단일 멤버 찾기 Name")
    void findByName(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        Member member = memberRepository.findByName("m1").get();

        assertThat(member.getName()).isEqualTo("m1");

    }

    @Test
    @DisplayName("단일 멤버 찾기 Name과 나이")
    void findByNameAndAge(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        Member member = memberRepository.findByNameAndAge("m1",10).get();

        assertThat(member.getName()).isEqualTo("m1");

    }

    @Test
    @DisplayName("멤버 리스트 찾기 기본")
    void findByAllBasic(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        List<Member> results = memberRepository.findAll();

        for(Member member: results){
            System.out.println(member);
        }

        assertThat(results.size()).isEqualTo(4);
    }
    @Test
    @DisplayName("멤버 리스트 찾기 단일 조건 정렬")
    void findByAllSortSingle(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "name");
        List<Member> results = memberRepository.findAll(sort);

        for(Member member: results){
            System.out.println(member);
        }

        assertThat(results.size()).isEqualTo(4);
    }
    @Test
    @DisplayName("멤버 리스트 찾기 여러가지 정렬")
    void findByAllSortMany(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        List<Sort.Order> orders = List.of(Sort.Order.desc("name"),Sort.Order.asc("age"));
        List<Member> results = memberRepository.findAll(Sort.by(orders));

        for(Member member: results){
            System.out.println(member);
        }

        assertThat(results.size()).isEqualTo(4);
    }
    @Test
    @DisplayName("멤버 리스트 찾기 여러가지 페이징")
    void findByAllPaging(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        PageRequest pageRequest1 = PageRequest.of(0, 2);
        PageRequest.of(0,2,Sort.by(Sort.Direction.DESC,"name"));
        PageRequest.of(0,2,Sort.Direction.DESC,"name");

        Slice<Member> result1 = memberRepository.findAll(pageRequest1);
        Page<Member> result2 = memberRepository.findAll(pageRequest1);

        List<Member> content1 = result1.getContent();
        List<Member> content2 = result2.getContent();

        assertThat(content1.size()).isEqualTo(2);
        assertThat(content2.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("멤버리스트 조회 개수 정하기")
    void findTopAndFirst(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        List<Member> memberList = memberRepository.findFirst3By();

        for(Member member: memberList){
            System.out.println(member);
        }

        assertThat(memberList.size()).isEqualTo(3);

    }

    @Test
    @DisplayName("멤버리스트 조회 개수 정하기 정렬 시행")
    void findTopAndFirstSort(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        List<Member> memberList = memberRepository.findFirst3ByOrderByNameDesc();

        for(Member member: memberList){
            System.out.println(member);
        }

        assertThat(memberList.size()).isEqualTo(3);

    }

    @Test
    @DisplayName("대소문자 구분하지 않고 조회")
    void findIgnoreCase(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        Member result = memberRepository.findByNameIgnoreCase("M1").get();

        System.out.println(result);

        assertThat(result.getName()).isEqualTo("m1");
    }

    @Test
    @DisplayName("나이가 더 큰 것 after 이용")
    void findAfter(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        List<Member> memberList =  memberRepository.findByAgeAfter(11);

        for(Member member: memberList){
            System.out.println(member);
        }

        assertThat(memberList.size()).isEqualTo(3);

    }

    @Test
    @DisplayName("나이가 더 큰 것 대소 비교 이용")
    void findOperator(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        List<Member> memberList =  memberRepository.findByAgeGreaterThan(11);

        for(Member member: memberList){
            System.out.println(member);
        }

        assertThat(memberList.size()).isEqualTo(4);

    }

    @Test
    @DisplayName("사이 값 Between 사용")
    void findBetween(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        List<Member> memberList =  memberRepository.findByAgeBetween(11,14);

        for(Member member: memberList){
            System.out.println(member);
        }

        assertThat(memberList.size()).isEqualTo(3);

    }

    @Test
    @DisplayName("값들에 포함되는지 in 사용")
    void findIn(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        List<Member> memberList =  memberRepository.findByAgeIn(List.of(11,14));

        for(Member member: memberList){
            System.out.println(member);
        }

        assertThat(memberList.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("age가 Null 아닌 것 찾기")
    void findNotNull(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        List<Member> memberList =  memberRepository.findByAgeNotNull();

        for(Member member: memberList){
            System.out.println(member);
        }

        assertThat(memberList.size()).isEqualTo(4);

    }

    @Test
    @DisplayName("Like 사용해서 문자열 포함하는 것 찾기")
    void findLike(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        List<Member> memberList =  memberRepository.findByNameLike("m%");

        for(Member member: memberList){
            System.out.println(member);
        }

        assertThat(memberList.size()).isEqualTo(4);

    }

    @Test
    @DisplayName("EndingWith 사용해서 끝 문자열을 포함하는 것 찾기")
    void findWith(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        List<Member> memberList =  memberRepository.findByNameEndingWith("3");

        for(Member member: memberList){
            System.out.println(member);
        }

        assertThat(memberList.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("Contains 사용해서 문자열을 포함하는 것 찾기")
    void findContain(){
        List<Member> members = createMember();

        for(Member member: members){
            memberRepository.save(member);
        }

        List<Member> memberList =  memberRepository.findByNameContains("m");

        for(Member member: memberList){
            System.out.println(member);
        }

        assertThat(memberList.size()).isEqualTo(4);

    }



    private List<Member> createMember() {
        Member m1 = Member.builder().name("m1").age(12).build();
        Member m2 = Member.builder().name("m3").age(13).build();
        Member m3 = Member.builder().name("m2").age(12).build();
        Member m4 = Member.builder().name("m4").age(14).build();
        return List.of(m1,m2,m3,m4);
    }
}

