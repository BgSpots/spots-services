package com.spots.service.common;

import com.spots.domain.DatabaseSequence;
import com.spots.repository.DatabaseSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {
    private final DatabaseSequenceRepository databaseSequenceRepository;

    public long generateSequence(String seqName) {
        final var counter =
                databaseSequenceRepository
                        .findById(seqName)
                        .orElseGet(() -> new DatabaseSequence(seqName, 1L));
        final var index = counter.getSeq();
        counter.setSeq(index + 1);
        databaseSequenceRepository.save(counter);
        return index;
    }
}
