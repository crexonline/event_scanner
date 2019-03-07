package io.lastwill.tronishairdrop.model.domain.register;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "snapshot_tronsnapshoteos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EosSnapshotEntry extends AbstractSnapshotEntry {
    @Column(nullable = false, unique = true)
    private String eosAddress;
    private String tronAddress;
}
