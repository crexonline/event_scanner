package com.glowstick.neocli4j;

import lombok.Getter;
import java.util.List;

@Getter
public class Block {
    private final String hash;
    private final Long timeSeconds;
    private final List<Transaction> transactions;

    public Block(String hash, Long timeSeconds, List<Transaction> transactions) {
        this.hash = hash;
        this.timeSeconds = timeSeconds;
        this.transactions = transactions;
    }
}
