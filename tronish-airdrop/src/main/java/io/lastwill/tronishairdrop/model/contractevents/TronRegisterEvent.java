package io.lastwill.tronishairdrop.model.contractevents;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class TronRegisterEvent extends ContractEvent {
    private String tronAddress;

    public TronRegisterEvent(ContractEventDefinition definition, String address, String tronAddress) {
        super(definition, address);
        this.tronAddress = tronAddress;
    }
}
