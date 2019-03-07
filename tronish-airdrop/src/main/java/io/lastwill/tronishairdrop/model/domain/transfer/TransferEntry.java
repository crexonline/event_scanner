package io.lastwill.tronishairdrop.model.domain.transfer;

import io.lastwill.eventscan.model.CryptoCurrency;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@Entity
@Table(name = "snapshot_tronsnapshottransfer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TransferEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String from;
    @Column(nullable = false)
    private String to;
    @Column(nullable = false)
    private BigInteger amount;
    @Column(nullable = false)
    private String txHash;
    @Column(nullable = false)
    private long blockNumber;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CryptoCurrency currency;
}
