package io.lastwill.tronishairdrop.repositories;

import io.lastwill.tronishairdrop.model.domain.transfer.TransferEntry;
import org.springframework.data.repository.CrudRepository;

public interface TransferEntryRepository extends CrudRepository<TransferEntry, Integer> {
}
