package com.qa.Repository;


import com.qa.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUsernameAndPassword(String username, String Password);
    List<Account> findAllByUsername(String username);
    List<Account> findAccountsById(Long id);
}

