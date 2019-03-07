package io.lastwill.tronishairdrop.model.contractevents;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class EthRegisterEvent extends ContractEvent {
    private String ethAddress;
    private String tronAddress;

    public EthRegisterEvent(ContractEventDefinition definition, String address, String ethAddress, String tronAddress) {
        super(definition, address);
        this.ethAddress = ethAddress;
        this.tronAddress = tronAddress;
    }
}
