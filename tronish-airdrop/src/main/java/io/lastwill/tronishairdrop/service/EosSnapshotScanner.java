package io.lastwill.tronishairdrop.service;

import io.mywish.blockchain.WrapperBlock;
import io.mywish.eos.blockchain.services.EosNetwork;
import io.mywish.eos.blockchain.services.EosScannerPolling;
import io.mywish.scanner.services.LastBlockPersister;

import java.util.Objects;

public class EosSnapshotScanner extends EosScannerPolling {
    private final long snapshotBlock;

    public EosSnapshotScanner(
            EosNetwork network,
            LastBlockPersister lastBlockPersister,
            long pollingInterval,
            int commitmentChainLength,
            long snapshotBlock
    ) {
        super(network, lastBlockPersister, pollingInterval, commitmentChainLength, false);
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
