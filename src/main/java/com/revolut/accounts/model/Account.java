package com.revolut.accounts.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.locks.ReentrantLock;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private Double balance;

    private ReentrantLock lock;

    public Account(Long id, String name, Double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.lock = new ReentrantLock();
    }
}
