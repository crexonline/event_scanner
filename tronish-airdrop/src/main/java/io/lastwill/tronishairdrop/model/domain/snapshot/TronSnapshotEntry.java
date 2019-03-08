package io.lastwill.tronishairdrop.model.domain.snapshot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "snapshot_tronsnapshottron")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TronSnapshotEntry extends AbstractSnapshotEntry {
    @Column(nullable = false, unique = true)
    private String tronAddress;
}
