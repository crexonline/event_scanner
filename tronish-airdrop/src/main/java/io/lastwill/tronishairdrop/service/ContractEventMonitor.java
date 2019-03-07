package io.lastwill.tronishairdrop.service;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.services.TransactionProvider;
import io.lastwill.tronishairdrop.model.events.ContractEventsEvent;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Component
public class ContractEventMonitor {
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private TransactionProvider transactionProvider;

    @Value("${eos-snapshot-block")
    private long eosSnapshotBlock;
    @Value("${eth-snapshot-block}")
    private long ethSnapshotBlock;
    @Value("${tron-snapshot-block}")
    private long tronSnapshotBlock;

    private Map<NetworkType, Long> snapshotBlocks = new HashMap<>(3);

    @PostConstruct
    protected void init() {
        snapshotBlocks.put(NetworkType.EOS_MAINNET, eosSnapshotBlock);
        snapshotBlocks.put(NetworkType.ETHEREUM_MAINNET, ethSnapshotBlock);
        snapshotBlocks.put(NetworkType.TRON_MAINNET, tronSnapshotBlock);
    }
    @EventListener
    public void onNewBlock(final NewBlockEvent event) {
        // todo: restrictions after snapshot block number
        Set<String> addresses = event.getTransactionsByAddress().keySet();
        if (addresses.isEmpty()) {
            return;
        }

        if (!snapshotBlocks.keySet().contains(event.getNetworkType())) {
            return;
        }

        event.getTransactionsByAddress()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(transaction -> getReceiptOrNull(event.getNetworkType(), transaction))
                .filter(Objects::nonNull)
                .filter(WrapperTransactionReceipt::isSuccess)
                .map(receipt -> new ContractEventsEvent(event.getNetworkType(), receipt))
                .forEach(eventPublisher::publish);
    }

    public WrapperTransactionReceipt getReceiptOrNull(NetworkType networkType, WrapperTransaction transaction) {
        WrapperTransactionReceipt receipt = null;
        try {
            receipt = transactionProvider.getTransactionReceipt(networkType, transaction);
        } catch (Exception e) {
            log.error("Exception while getting transaction receipt: {}, {}", networkType, transaction.getHash());
        }
        return receipt;
    }
}
