package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.binart.trackmanagementsystem.model.StageSequence;

public interface StageSequenceService {

    StageSequence create(StageSequence stageSequence, Long userId);

    StageSequence findById(Long id);

    Page<StageSequence> findFiltered(String name, Pageable pageable);

}
