package io.lastwill.tronishairdrop.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.tronishairdrop.model.contractevents.EosRegisterEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.eos.blockchain.builders.ActionEventBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Getter
@NoArgsConstructor
public class EosRegisterEventBuilder extends ActionEventBuilder<EosRegisterEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition("put");

    @Override
    public EosRegisterEvent build(String address, ObjectNode data) {
        return new EosRegisterEvent(
                definition,
                address,
                data.get("eos_account").textValue(),
                data.get("tron_address").textValue()
        );
    }
}
