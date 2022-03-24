package com.example.blogjpa.member.repository;

import com.example.blogjpa.member.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByName(String name);

    Optional<Member> findByNameAndAge(String name, int age);

    //Optional<Member> findByNameAndAge(int age, String name); 오류 발생

    List<Member> findTop3By();

    List<Member> findFirst3By();

    List<Member> findByOrderByNameDesc();

    List<Member> findFirst3ByOrderByNameDesc();

    List<Member> findDistinctByAge(int age);

    Optional<Member> findByNameIgnoreCase(String n);

    List<Member> findByAgeAfter(int age);

    List<Member> findByAgeGreaterThan(int age);

    List<Member> findByAgeBetween(int from, int to);

    List<Member> findByAgeIn(List<Integer> list);

    List<Member> findByAgeNotNull();

    List<Member> findByNameLike(String name);

    List<Member> findByNameEndingWith(String name);

    List<Member> findByNameContains(String name);

    void deleteByNameLike(String s);

    @Query("select m from Member m")
    List<Member> findMemberBy();

    @Query("select m from Member m where m.name = ?1 and m.age = ?2")
    Optional<Member> findMemberToSequenceByNameAndAge(String name, int age);

    @Query("select m from Member m where m.name = :name and m.age = :age")
    Optional<Member> findMemberToTargetNotDesignateByNameAndAge(String name, int age);

    @Query("select m from Member m where m.name = :name and m.age = :age")
    Optional<Member> findMemberToTargetDesignateByNameAndAge(@Param("name") String n, @Param("age") int a);

    @Query("update Member m set m.age=m.age+1 where m.age>:age")
    int bulkNotModifyingByAgeGreaterThanPlus(int age);

    @Modifying
    @Query("update Member m set m.age=m.age+1 where m.age>:age")
    int bulkModifyingNotAttributeByAgeGreaterThanPlus(int age);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age=m.age+1 where m.age>:age")
    int bulkModifyingAttributeByAgeGreaterThanPlus(int age);


    Optional<Member> findNotFetchJoinByName(String name);

    @Query("select m from Member m join fetch m.team where m.name=:name")
    Optional<Member> findFetchJoinByName(String name);

    @EntityGraph(attributePaths = "team")
    Optional<Member> findEntityGraphFetchJoinByName(String name);

}
