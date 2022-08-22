package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.model.ExpirationNotification;
import uz.binart.trackmanagementsystem.repository.ExpirationNotificationRepository;
import uz.binart.trackmanagementsystem.service.ExpirationNotificationService;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpirationNotificationServiceImpl implements ExpirationNotificationService {

    private final ExpirationNotificationRepository expirationNotificationRepository;

    public ExpirationNotification save(ExpirationNotification expirationNotification){
        return expirationNotificationRepository.save(expirationNotification);
    }

    public Long totalNotSeen(){
        return expirationNotificationRepository.countAllByWasSeenFalse();
    }

    public List<ExpirationNotification> getActualNotifications(){
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return expirationNotificationRepository.findAllByWasSeenFalse(sort);
    }

    public List<ExpirationNotification> getAsList(Pageable pageable){
        return expirationNotificationRepository.findAll(pageable).getContent();
    }

    public Page<ExpirationNotification> getNotifications(Boolean wasSeen, Pageable pageable){
        if(wasSeen != null){
            return expirationNotificationRepository.findAll(filteringSpecification(wasSeen), pageable);
        }else
            return expirationNotificationRepository.findAll(pageable);
    }

    private Specification<ExpirationNotification> filteringSpecification(Boolean wasSeen){
        return ((root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("wasSeen"), wasSeen);
        });
    }

    public ExpirationNotification getById(Long id){
        if(!expirationNotificationRepository.existsById(id)){
            return null;
        }
        ExpirationNotification expirationNotification = expirationNotificationRepository.getOne(id);
        expirationNotification.setWasSeen(true);
        return expirationNotificationRepository.save(expirationNotification);
    }

    public ExpirationNotification getByIdAndMarkAsSeen(Long id){
        Optional<ExpirationNotification> notificationOptional = expirationNotificationRepository.findById(id);
        if(notificationOptional.isPresent()){
            ExpirationNotification expirationNotification = notificationOptional.get();
            if(!expirationNotification.getWasSeen()) {
                expirationNotification.setWasSeen(true);
                expirationNotificationRepository.save(expirationNotification);
            }
            return expirationNotification;
        }else throw new NotFoundException();
    }
}
