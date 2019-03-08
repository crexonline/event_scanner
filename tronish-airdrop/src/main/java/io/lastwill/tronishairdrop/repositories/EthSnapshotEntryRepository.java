package io.lastwill.tronishairdrop.repositories;

import io.lastwill.tronishairdrop.model.domain.snapshot.EthSnapshotEntry;
import org.springframework.data.repository.CrudRepository;

public interface EthSnapshotEntryRepository extends CrudRepository<EthSnapshotEntry, Integer> {
    boolean existsByEthAddress(String ethAddress);
}
