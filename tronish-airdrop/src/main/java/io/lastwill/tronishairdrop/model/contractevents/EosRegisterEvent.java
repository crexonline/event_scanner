package io.lastwill.tronishairdrop.model.contractevents;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class EosRegisterEvent extends ContractEvent {
    private final String eosAccount;
    private final String tronAddress;

    public EosRegisterEvent(ContractEventDefinition definition, String address, String eosAccount, String tronAddress) {
        super(definition, address);
        this.eosAccount = eosAccount;
        this.tronAddress = tronAddress;
    }
}
