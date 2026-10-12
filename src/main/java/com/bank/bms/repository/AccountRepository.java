package com.bank.bms.repository;

import com.bank.bms.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(String accountNumber);

    @Query("select a from Account a join fetch a.owner where a.owner.username = :username")
    List<Account> findAllByOwnerUsername(@Param("username") String username);

    /** Row-level lock so concurrent debits/credits cannot corrupt a balance. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a join fetch a.owner where a.accountNumber = :accountNumber")
    Optional<Account> findForUpdate(@Param("accountNumber") String accountNumber);

    @Query("select a from Account a join fetch a.owner where a.accountNumber = :accountNumber")
    Optional<Account> findWithOwner(@Param("accountNumber") String accountNumber);
}
