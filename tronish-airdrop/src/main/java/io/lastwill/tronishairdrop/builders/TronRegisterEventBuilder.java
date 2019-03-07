package io.lastwill.tronishairdrop.builders;

import io.lastwill.tronishairdrop.model.contractevents.TronRegisterEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.tron.blockchain.builders.TronEventBuilder;
import io.mywish.troncli4j.model.EventResult;
import org.springframework.stereotype.Component;

@Component
public class TronRegisterEventBuilder extends TronEventBuilder<TronRegisterEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition("RegisterAdd");

    @Override
    public TronRegisterEvent build(String address, EventResult event) {
        return new TronRegisterEvent(DEFINITION, address, event.getResult().get("tronAddress"));
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}
