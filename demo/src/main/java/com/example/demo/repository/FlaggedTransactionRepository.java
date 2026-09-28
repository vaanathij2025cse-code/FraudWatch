package com.example.demo.repository;

import com.example.demo.model.FlaggedTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlaggedTransactionRepository extends JpaRepository<FlaggedTransaction, Long> {

}
