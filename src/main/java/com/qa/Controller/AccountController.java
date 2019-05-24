package com.qa.Controller;

import com.qa.Repository.AccountRepository;
import com.qa.model.Account;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/account/")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public Long create(@RequestBody Account account) {
        if (accountRepository.findAllByUsername(account.getUsername()).isEmpty()){
            account.setBalance(0);
            account.setTransaction(0);
            accountRepository.saveAndFlush(account);
            return account.getId();
        }
        else{
            return 0L;
        }
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Long login(@RequestBody Account account) {
        List<Account> matchingUser=accountRepository.findByUsernameAndPassword(account.getUsername(),account.getPassword());
        if (!(matchingUser.isEmpty())){
            return matchingUser.get(0).getId();
        }else{
            return 0L;
        }
    }

    @RequestMapping(value = "update/{id}", method = RequestMethod.POST)
    public void update(@PathVariable Long id, @RequestBody Account account) {
        Account existingUser = accountRepository.findOne(id);
        account.setId(id);
        BeanUtils.copyProperties(account, existingUser);
        accountRepository.saveAndFlush(account);
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    public int delete(@PathVariable Long id)
    {
        if(accountRepository.findAccountsById(id).isEmpty()){
            return 0;
        }else{
            accountRepository.delete(id);
            return 1;
        }

    }

    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    public Account list(@PathVariable Long id)
    {
        if (!accountRepository.findAccountsById(id).isEmpty()){
            Account account =  accountRepository.findAccountsById(id).get(0);
            account.setPassword("");
            return account;
        }else{
            Account nope = new Account(0, 0, "", "");
            nope.setId(0L);
            return nope;
        }
    }

    @RequestMapping(value = "withdraw", method = RequestMethod.POST)
    public int withdraw(@RequestBody Account account){
        Account target = accountRepository.findOne(account.getId());
        double amount = account.getTransaction();
        if ((target.getBalance() >= amount) && (amount > 0) ){
            target.setBalance(truncate(target.getBalance()-amount));
            accountRepository.saveAndFlush(target);
            return 1;
        }
        return 0;
    }

    @RequestMapping(value = "deposit", method = RequestMethod.POST)
    public int deposit(@RequestBody Account account){
        Account target = accountRepository.findOne(account.getId());
        double amount = account.getTransaction();
        if (amount > 0){
            target.setBalance(truncate(target.getBalance() + amount));
            accountRepository.saveAndFlush(target);
            return 1;
        }
        return 0;
    }

    @RequestMapping(value = "transfer/{username}", method = RequestMethod.POST)
    public int transfer(@PathVariable String username, @RequestBody Account account){
        Account sender = accountRepository.findOne(account.getId());
        double amount=account.getTransaction();
        boolean receiverExists = !(accountRepository.findAllByUsername(username).isEmpty());
        boolean hasEnough = sender.getBalance() >= amount;
        boolean differentIds = receiverExists && !(accountRepository.findAllByUsername(username).get(0).getId().equals(account.getId()));
        boolean validAmount =  amount > 0 ;
        if (hasEnough && differentIds && validAmount && receiverExists){
            Account receiver = accountRepository.findAllByUsername(username).get(0);
            sender.setBalance(truncate(sender.getBalance() - amount));
            receiver.setBalance(truncate(receiver.getBalance() + amount));
            accountRepository.saveAndFlush(sender);
            accountRepository.saveAndFlush(receiver);
            return 1;
        }
        return 0;
    }

    private double truncate(double value){
        return Math.floor(value*100)/100;
    }
}
