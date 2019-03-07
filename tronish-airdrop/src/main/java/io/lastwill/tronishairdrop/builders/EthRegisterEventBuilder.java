package io.lastwill.tronishairdrop.builders;

import io.lastwill.tronishairdrop.model.contractevents.EthRegisterEvent;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;

import java.util.Arrays;
import java.util.List;

@Getter
@Component
@NoArgsConstructor
public class EthRegisterEventBuilder extends Web3ContractEventBuilder<EthRegisterEvent> {
    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "RegisterAdd",
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Address.class, true)
            )
    );

    @Override
    public EthRegisterEvent build(String address, List<Object> values) {
        return new EthRegisterEvent(
                definition,
                address,
                (String) values.get(0),
                (String) values.get(1)
        );
    }
}
