package io.lastwill.tronishairdrop.model.events;

import io.lastwill.eventscan.events.model.BaseEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public class ContractEventsEvent extends BaseEvent {
    private final WrapperTransactionReceipt transactionReceipt;
    private final WrapperBlock block;

    public ContractEventsEvent(NetworkType networkType, WrapperTransactionReceipt transactionReceipt, WrapperBlock block) {
        super(networkType);
        this.transactionReceipt = transactionReceipt;
        this.block = block;
    }
}
