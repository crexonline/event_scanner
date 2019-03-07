package io.lastwill.tronishairdrop.service;

import io.lastwill.eventscan.events.model.contract.eos.EosTransferEvent;
import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.tronishairdrop.model.domain.transfer.TransferEntry;
import io.lastwill.tronishairdrop.model.events.ContractEventsEvent;
import io.lastwill.tronishairdrop.repositories.EosSnapshotEntryRepository;
import io.lastwill.tronishairdrop.repositories.EthSnapshotEntryRepository;
import io.lastwill.tronishairdrop.repositories.TransferEntryRepository;
import io.mywish.blockchain.ContractEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Slf4j
@Component
public class TransfersHandler {
    @Value("${eosish-account}")
    private String eosishAccount;
    @Value("${eosish-token-symbol}")
    private String eosishTokenSymbol;
    @Value("${wish-address}")
    private String wishAddress;

    @Autowired
    private EosSnapshotEntryRepository eosSnapshotEntryRepository;
    @Autowired
    private EthSnapshotEntryRepository ethSnapshotEntryRepository;
    @Autowired
    private TransferEntryRepository transferEntryRepository;

    @EventListener
    public void handleEosishTransfers(final ContractEventsEvent contractsEventsEvent) {
        if (contractsEventsEvent.getNetworkType() != NetworkType.EOS_MAINNET) {
            return;
        }

        getContractEventsStream(contractsEventsEvent)
                .filter(event -> event instanceof EosTransferEvent)
                .map(event -> (EosTransferEvent) event)
                .filter(event -> eosishAccount.equalsIgnoreCase(event.getAddress()))
                .filter(event -> eosishTokenSymbol.equalsIgnoreCase(event.getSymbol()))
//                .filter(event -> eosSnapshotEntryRepository.existsByEosAddresses(event.getFrom(), event.getTo()))
                .map(event -> new TransferEntry(
                        null,
                        event.getFrom(),
                        event.getTo(),
                        event.getTokens(),
                        contractsEventsEvent.getTransactionReceipt().getTransactionHash(),
                        contractsEventsEvent.getBlock().getNumber(),
                        CryptoCurrency.EOSISH
                ))
                .forEach(transferEntryRepository::save);
    }

    @EventListener
    public void handleWishTransfers(final ContractEventsEvent contractsEventsEvent) {
        if (contractsEventsEvent.getNetworkType() != NetworkType.ETHEREUM_MAINNET) {
            return;
        }

        getContractEventsStream(contractsEventsEvent)
                .filter(event -> event instanceof TransferEvent)
                .map(event -> (TransferEvent) event)
                .filter(event -> wishAddress.equalsIgnoreCase(event.getAddress()))
//                .filter(event -> ethSnapshotEntryRepository.existsByEthAddresses(event.getFrom(), event.getTo()))
                .map(event -> new TransferEntry(
                        null,
                        event.getFrom(),
                        event.getTo(),
                        event.getTokens(),
                        contractsEventsEvent.getTransactionReceipt().getTransactionHash(),
                        contractsEventsEvent.getBlock().getNumber(),
                        CryptoCurrency.WISH
                ))
                .forEach(transferEntryRepository::save);

    }

    // todo: TRX transfers

    private Stream<ContractEvent> getContractEventsStream(ContractEventsEvent contractEventsEvent) {
        return contractEventsEvent
                .getTransactionReceipt()
                .getLogs()
                .stream();
    }
}
