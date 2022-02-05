package com.example.demo.repository;

import com.example.demo.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamJpaRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);
}
