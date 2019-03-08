package io.lastwill.tronishairdrop.service;

import io.mywish.blockchain.WrapperBlock;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.web3.blockchain.service.Web3Network;
import io.mywish.web3.blockchain.service.Web3Scanner;

import java.util.Objects;

public class EthSnapshotScanner extends Web3Scanner {
    private final long snapshotBlock;

    public EthSnapshotScanner(
            Web3Network network,
            LastBlockPersister lastBlockPersister,
            long pollingInterval,
            int commitmentChainLength,
            long snapshotBlock
    ) {
        super(network, lastBlockPersister, pollingInterval, commitmentChainLength);
        this.snapshotBlock = snapshotBlock;
    }

    @Override
    protected void processBlock(WrapperBlock block) {
        super.processBlock(block);
        if (Objects.equals(lastBlockPersister.getLastBlock(), snapshotBlock)) {
            close();
        }
    }
}
