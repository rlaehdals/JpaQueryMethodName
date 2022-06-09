package com.example.blogjpa.querydsl;


import com.example.blogjpa.member.QuerydslConfig;
import com.example.blogjpa.member.domain.Dto;
import com.example.blogjpa.member.domain.Member;
import com.example.blogjpa.member.domain.QDto;
import com.example.blogjpa.member.domain.QMember;
import com.example.blogjpa.member.repository.MemberRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.blogjpa.member.domain.QMember.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
public class QuerydslTest {

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    void init() {
        Member member1 = createMember("hi1", 10);
        Member member2 = createMember("hi2", 11);
        Member member3 = createMember("hi3", 12);
        Member member4 = createMember("hi4", 13);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

    }

    @Test
    void select() {

        Member result1 = jpaQueryFactory.select(member)
                .from(member).fetchOne();

        Member result2 = jpaQueryFactory.selectFrom(member)
                .fetchOne();
    }

    @Test
    void returnResult() {
        Member result1 = jpaQueryFactory.selectFrom(member)
                .fetchOne();

        Member result2 = jpaQueryFactory.selectFrom(member)
                .fetchFirst();

        List<Member> result3 = jpaQueryFactory.selectFrom(member)
                .fetch();
    }

    @Test
    void findName() {

        Member result = jpaQueryFactory.select(member)
                .from(member)
                .where(member.name.eq("hi1"))
                .fetchOne();
        assertThat(result.getAge()).isEqualTo(10);
    }

    @Test
    void findNameAndAge() {
        Member result1 = jpaQueryFactory.select(member)
                .from(member)
                .where(member.name.eq("hi1").and(member.age.eq(10)))
                .fetchOne();

        Member result2 = jpaQueryFactory.select(member)
                .from(member)
                .where(member.name.eq("hi1"), member.age.eq(10))
                .fetchOne();

        assertThat(result1.getAge()).isEqualTo(10);
        assertThat(result2.getAge()).isEqualTo(10);
    }

    @Test
    void notEqual() {
        List<Member> results = jpaQueryFactory.selectFrom(member)
                .where(member.name.ne("hi1")).fetch();

        assertThat(results.size()).isEqualTo(3);
    }

    @Test
    void in() {
        List<Member> results = jpaQueryFactory.selectFrom(member)
                .where(member.age.in(10, 11)).fetch();

        assertThat(results.size()).isEqualTo(2);
    }

    @Test
    void like() {
        Member result = jpaQueryFactory.selectFrom(member)
                .where(member.name.like("%3")).fetchOne();

        assertThat(result.getName()).isEqualTo("hi3");
    }

    @Test
    void contains() {
        Member result = jpaQueryFactory.selectFrom(member)
                .where(member.name.contains("3"))
                .fetchOne();

        assertThat(result.getName()).isEqualTo("hi3");
    }


    @Test
    void sort() {
        List<Member> fetch = jpaQueryFactory.selectFrom(member)
                .orderBy(member.age.desc().nullsLast())
                .orderBy(member.name.asc())
                .fetch();

        assertThat(fetch.get(0).getAge()).isEqualTo(13);
    }

    @Test
    void page() {
        List<Member> result = jpaQueryFactory.selectFrom(member)
                .orderBy(member.age.desc())
                .offset(0)
                .limit(2)
                .fetch();

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("hi4");
    }

    @Test
    void aggregation() {
        List<Tuple> tuples = jpaQueryFactory.select(member.age.sum(),
                member.age.avg(),
                member.age.count(),
                member.age.max(),
                member.age.min()).from(member)
                .fetch();

        assertThat(tuples.size()).isEqualTo(1);
        Tuple result = tuples.get(0);

        assertThat(result.get(member.age.count())).isEqualTo(4);
        assertThat(result.get(member.age.max())).isEqualTo(13);
        assertThat(result.get(member.age.min())).isEqualTo(10);
    }

    @Test
    void groupBy() {
        memberRepository.save(createMember("hi5", 10));
        memberRepository.save(createMember("hi6", 10));
        List<Long> result = jpaQueryFactory.select(member.age.count())
                .from(member)
                .orderBy(member.age.asc())
                .groupBy(member.age)
                .having(member.age.count().goe(2))
                .fetch();

        assertThat(result.get(0)).isEqualTo(3);
        assertThat(result.size()).isEqualTo(1);
    }

//    @Test
//    void join(){
//        List<Member> result =jpaQueryFactory.selectFrom(member)
//                .join(member.club, club)
//                .where(club.name.eq("smu"))
//                .fetch();
//
//    }

//    @Test
//    void fetchJoin() {
//        List<Member> result = jpaQueryFactory.selectFrom(member)
//                .join(member, club).fetchJoin()
//                .where(club.name.eq("smu"))
//                .fetch();
//    }

//
//    @Test
//    void thetaJoin(){
//        List<Member> result =jpaQueryFactory.selectFrom(member)
//                .join(member,club)
//                .where(member.name.eq(club.name))
//                .fetch();

//    }
//    @Test
//    void onJoin(){
//        List<Tuple> result =jpaQueryFactory.selectFrom(member)
//                .join(member,club)
//                .leftJoin(group).on(member.name.eq(group.name))
//                .fetch();
//    }

    @Test
    void subQuery() {

        QMember subMember = new QMember("subMember");

        List<Member> result = jpaQueryFactory.selectFrom(member)
                .where(member.age.eq(JPAExpressions.select(subMember.age.max()).from(subMember)))
                .fetch();

        assertThat(result.get(0).getAge()).isEqualTo(13);
    }

    @Test
    void caseWhen() {
        List<String> result = jpaQueryFactory.
                select(member.age
                        .when(10).then("10살")
                        .when(11).then("11살")
                        .otherwise("기타"))
                .from(member)
                .orderBy(member.name.asc()).fetch();

        assertThat(result.get(3)).isEqualTo("기타");

    }

    @Test
    void caseWhen2() {
        List<String> result = jpaQueryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(10, 11)).then("10~11살")
                        .otherwise("기타"))
                .from(member)
                .orderBy(member.name.asc()).fetch();

        assertThat(result.get(3)).isEqualTo("기타");
    }

    @Test
    void constEx() {
        Tuple tuple = jpaQueryFactory
                .select(member.name, Expressions.constant("A"))
                .from(member)
                .fetchFirst();

        assertThat(tuple.get(member.name)).isEqualTo("hi1");
        assertThat(tuple.get(Expressions.constant("A"))).isEqualTo("A");
    }

    @Test
    void concat() {
        String result = jpaQueryFactory
                .select(member.name.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.name.eq("hi1"))
                .fetchOne();

        assertThat(result).isEqualTo("hi1_10");
    }

    @Test
    void projection() {
        // setter사용
        Dto result1 = jpaQueryFactory
                .select(Projections.bean(Dto.class,
                        member.name.as("username"),
                        member.age))
                .from(member)
                .where(member.name.eq("hi1"))
                .fetchOne();

        // field 직접 접근
        Dto result2 = jpaQueryFactory
                .select(Projections.fields(Dto.class,
                        member.name.as("username"),
                        member.age))
                .from(member)
                .where(member.name.eq("hi1"))
                .fetchOne();

        // 생성자 사용
        Dto result3 = jpaQueryFactory
                .select(Projections.constructor(Dto.class,
                        member.name.as("username"),
                        member.age))
                .from(member)
                .where(member.name.eq("hi1"))
                .fetchOne();

        // @QueryProjection 사용
        Dto result4 = jpaQueryFactory
                .select(new QDto(member.name, member.age))
                .from(member)
                .where(member.name.eq("hi1"))
                .fetchOne();


        assertThat(result1.getAge()).isEqualTo(10);
        assertThat(result2.getAge()).isEqualTo(10);
        assertThat(result3.getAge()).isEqualTo(10);
        assertThat(result4.getAge()).isEqualTo(10);

    }

    @Test
    void distinct() {
        List<String> fetch = jpaQueryFactory.select(member.name).distinct()
                .from(member)
                .fetch();
    }

    @Test
    void dynamicQuery() {
        List<Member> hi1 = findUser("hi1", 10);
        assertThat(hi1.size()).isEqualTo(1);
    }

    @Test
    void update() {
        long count = jpaQueryFactory
                .update(member)
                .set(member.age, member.age.add(10))
                .where(member.name.eq("hi1"))
                .execute();

        em.flush();
        em.clear();

    }

    @Test
    void utilPage() {
        PageRequest pageable = PageRequest.of(0, 2);

        List<Member> content = jpaQueryFactory
                .selectFrom(member)
                .limit(pageable.getPageSize())
                .offset(1)
                .fetch();


        Long totalCount = jpaQueryFactory.select(Wildcard.count)
                .from(member)
                .fetch().get(0);

        Page<Member> page = PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

        List<Member> results = page.getContent();
        int totalPages = page.getTotalPages();
        boolean hasNext = totalPages > pageable.getPageNumber();

        assertThat(hasNext).isTrue();
        assertThat(totalPages).isEqualTo(2);
        assertThat(results.size()).isEqualTo(2);
    }


    private List<Member> findUser(String nameCondition, Integer ageCondition) {
        return jpaQueryFactory
                .selectFrom(member)
                .where(nameEq(nameCondition), ageEq(ageCondition))
                .fetch();
    }

    private BooleanExpression nameEq(String nameCondition) {
        return nameCondition != null ? member.name.eq(nameCondition) : null;
    }

    private BooleanExpression ageEq(Integer ageCondition) {
        return ageCondition != null ? member.age.eq(ageCondition) : null;
    }

    private static Member createMember(String name, int age) {
        return Member.builder().name(name).age(age).build();
    }
}
