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
@Table(name = "snapshot_tronsnapshoteth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EthSnapshotEntry extends AbstractSnapshotEntry {
    @Column(nullable = false, unique = true)
    private String ethAddress;
    private String tronAddress;
}
