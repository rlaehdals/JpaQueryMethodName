package com.example.blogjpa.member.repository;

import com.example.blogjpa.member.domain.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);

    @EntityGraph(attributePaths = "member")
    Optional<Team> findListById(Long id);

}
