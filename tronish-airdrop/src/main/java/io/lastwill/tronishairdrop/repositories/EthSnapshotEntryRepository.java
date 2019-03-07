package io.lastwill.tronishairdrop.repositories;

import io.lastwill.tronishairdrop.model.domain.register.EthSnapshotEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EthSnapshotEntryRepository extends CrudRepository<EthSnapshotEntry, Integer> {
    boolean existsByEthAddress(String ethAddress);

    @Query("select e from EthSnapshotEntry where ethAddress in :addresses")
    boolean existsByEthAddresses(@Param("addresses") String... addresses);
}
