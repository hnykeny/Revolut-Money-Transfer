package com.revolut.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private Double balance;
}
