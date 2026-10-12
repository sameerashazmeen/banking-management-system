package com.bank.bms.repository;

import com.bank.bms.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TransactionRepository extends JpaRepository<BankTransaction, Long> {

    @Query("select t from BankTransaction t " +
           "where t.fromAccount = :acc or t.toAccount = :acc order by t.createdAt desc, t.id desc")
    List<BankTransaction> findHistory(@Param("acc") String accountNumber);
}
