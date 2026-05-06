package com.example.project.repository;

import com.example.project.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findContractById(Long id);

    boolean existsByNumber(Integer number);
}
