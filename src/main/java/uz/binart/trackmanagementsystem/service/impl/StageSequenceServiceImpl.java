package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.exception.IllegalChangeAttemptException;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.model.StageSequence;
import uz.binart.trackmanagementsystem.repository.StageSequenceRepository;
import uz.binart.trackmanagementsystem.service.ActionService;
import uz.binart.trackmanagementsystem.service.SequenceService;
import uz.binart.trackmanagementsystem.service.StageSequenceService;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StageSequenceServiceImpl implements StageSequenceService {

    private final StageSequenceRepository stageSequenceRepository;
    private final SequenceService sequenceService;
    private final ActionService actionService;

    public StageSequence create(StageSequence stageSequence, Long userId){

        if(stageSequence.getId() != null && stageSequenceRepository.existsById(stageSequence.getId()))
            throw new IllegalChangeAttemptException();
        sequenceService.updateSequence("stage_sequences");

        StageSequence savedStageSequence = stageSequenceRepository.save(stageSequence);
        actionService.captureCreate(savedStageSequence, "stage_sequences", userId);
        return savedStageSequence;
    }

    public StageSequence findById(Long id){

        if(!stageSequenceRepository.existsById(id)){
            throw new NotFoundException("no sequence with such id");
        }

        return stageSequenceRepository.getOne(id);
    }

    public Page<StageSequence> findFiltered(String name, Pageable pageable){
        return stageSequenceRepository.findAll(getFilteringSpecification(name), pageable);
    }

    private Specification<StageSequence> getFilteringSpecification(String name){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>(1);
            if(name != null)
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

}
