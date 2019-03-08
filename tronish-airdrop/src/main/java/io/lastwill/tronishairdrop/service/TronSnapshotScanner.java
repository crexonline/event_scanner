package io.lastwill.tronishairdrop.service;

import io.mywish.blockchain.WrapperBlock;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.tron.blockchain.services.TronNetwork;
import io.mywish.tron.blockchain.services.TronScanner;

import java.util.Objects;

public class TronSnapshotScanner extends TronScanner {
    private final long snapshotBlock;

    public TronSnapshotScanner(
            TronNetwork network,
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
