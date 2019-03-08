package io.lastwill.tronishairdrop.repositories;

import io.lastwill.tronishairdrop.model.domain.snapshot.EosSnapshotEntry;
import org.springframework.data.repository.CrudRepository;

public interface EosSnapshotEntryRepository extends CrudRepository<EosSnapshotEntry, Integer> {
    boolean existsByEosAddress(String eosAddress);
}
