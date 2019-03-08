package io.lastwill.tronishairdrop.service;

import io.lastwill.tronishairdrop.model.domain.snapshot.EosSnapshotEntry;
import io.lastwill.tronishairdrop.model.domain.snapshot.EthSnapshotEntry;
import io.lastwill.tronishairdrop.model.events.ContractEventsEvent;
import io.lastwill.tronishairdrop.model.contractevents.EosRegisterEvent;
import io.lastwill.tronishairdrop.model.contractevents.EthRegisterEvent;
import io.lastwill.tronishairdrop.model.contractevents.TronRegisterEvent;
import io.lastwill.tronishairdrop.model.domain.snapshot.TronSnapshotEntry;
import io.lastwill.tronishairdrop.repositories.EosSnapshotEntryRepository;
import io.lastwill.tronishairdrop.repositories.EthSnapshotEntryRepository;
import io.lastwill.tronishairdrop.repositories.TronSnapshotEntryRepository;
import io.mywish.blockchain.ContractEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@Profile("reg-fetcher")
public class RegisterHandler {
    @Value("${eos-register-address}")
    private String eosRegisterAddress;
    @Value("${eth-register-address}")
    private String ethRegisterAddress;
    @Value("${tron-register-address}")
    private String tronRegisterAddress;

    @Autowired
    private EosSnapshotEntryRepository eosRepository;
    @Autowired
    private EthSnapshotEntryRepository ethRepository;
    @Autowired
    private TronSnapshotEntryRepository tronRepository;

    @EventListener
    public void onNewContractEvent(final ContractEventsEvent event) {
        event.getTransactionReceipt()
                .getLogs()
                .forEach(this::handleContractEvent);
    }

    private void handleContractEvent(ContractEvent contractEvent) {
        if (contractEvent instanceof EosRegisterEvent) {
            handleEosRegisterEvent((EosRegisterEvent) contractEvent);
        } else if (contractEvent instanceof EthRegisterEvent) {
            handleEthRegisterEvent((EthRegisterEvent) contractEvent);
        } else if (contractEvent instanceof TronRegisterEvent) {
            handleTronRegisterEvent((TronRegisterEvent) contractEvent);
        }
    }

    private void handleEosRegisterEvent(EosRegisterEvent event) {
        if (!Objects.equals(event.getAddress(), eosRegisterAddress)) {
            return;
        }

        String eosAccount = event.getEosAccount().trim().toLowerCase();
        String tronAddress = event.getTronAddress().trim().toLowerCase();

        if (eosRepository.existsByEosAddress(eosAccount)) {
            log.error("Entry with eos account '{}' already exists in snapshot database.", eosAccount);
            return;
        }

        eosRepository.save(new EosSnapshotEntry(eosAccount, tronAddress));
    }

    private void handleEthRegisterEvent(EthRegisterEvent event) {
        if (!Objects.equals(event.getAddress(), ethRegisterAddress)) {
            return;
        }

        String ethAddress = event.getEthAddress().trim().toLowerCase();
        String tronAddress = event.getTronAddress().trim().toLowerCase().replace("0x", "41");

        if (ethRepository.existsByEthAddress(ethAddress)) {
            log.error("Entry with eth address '{}' already exists in snapshot database.", ethAddress);
            return;
        }

        ethRepository.save(new EthSnapshotEntry(ethAddress, tronAddress));
    }

    private void handleTronRegisterEvent(TronRegisterEvent event) {
        if (!Objects.equals(event.getAddress(), tronRegisterAddress)) {
            return;
        }

        String tronAddress = event.getTronAddress().trim().toLowerCase().replace("0x", "41");

        if (tronRepository.existsByTronAddress(tronAddress)) {
            log.error("Entry with tron address '{}' already exists in snapshot database.", tronAddress);
            return;
        }

        tronRepository.save(new TronSnapshotEntry(tronAddress));
    }
}
