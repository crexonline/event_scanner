package io.lastwill.tronishairdrop.repositories;

import io.lastwill.tronishairdrop.model.domain.register.TronSnapshotEntry;
import org.springframework.data.repository.CrudRepository;

public interface TronSnapshotEntryRepository extends CrudRepository<TronSnapshotEntry, Integer> {
    boolean existsByTronAddress(String tronAddress);
}
