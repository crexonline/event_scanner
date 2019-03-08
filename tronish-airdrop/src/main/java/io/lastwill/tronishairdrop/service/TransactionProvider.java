package io.lastwill.tronishairdrop.service;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionProvider {
    @Autowired
    private NetworkProvider networkProvider;

    public WrapperTransactionReceipt getTransactionReceipt(final NetworkType networkType,
                                                           final WrapperTransaction transaction) throws Exception {
        return networkProvider.get(networkType).getTxReceipt(transaction);
    }
}
