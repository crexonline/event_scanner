package io.lastwill.tronishairdrop.repositories;

import io.lastwill.tronishairdrop.model.domain.register.EosSnapshotEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EosSnapshotEntryRepository extends CrudRepository<EosSnapshotEntry, Integer> {
    boolean existsByEosAddress(String eosAddress);

    @Query("select e from EosSnapshotEntry where eosAddress in :addresses")
    boolean existsByEosAddresses(@Param("addresses") String... addresses);
}
