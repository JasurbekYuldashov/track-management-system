package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.exception.IllegalChangeAttemptException;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.model.Delivery;
import uz.binart.trackmanagementsystem.model.FileInformation;
import uz.binart.trackmanagementsystem.repository.DeliveryRepository;
import uz.binart.trackmanagementsystem.service.*;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final SequenceService sequenceService;
    private final ActionService actionService;
    private final FileInformationService fileInformationService;

    public Delivery save(Delivery delivery, Long userId){
        if(delivery.getId() != null && deliveryRepository.existsById(delivery.getId())){
            log.warn("trying to overwrite object with " + delivery.getId() + " in deliveries table");
            throw new IllegalChangeAttemptException();
        }
  //      sequenceService.updateSequence("pickups", "deliveries", "pickups_and_deliveries_id_seq");
        Delivery savedDelivery = deliveryRepository.save(delivery);
        actionService.captureCreate(savedDelivery, "deliveries", userId);
        return savedDelivery;
    }

    public Delivery getById(Long id){
        if(!deliveryRepository.existsById(id))
            throw new NotFoundException();
        return deliveryRepository.getOne(id);
    }

    public void deleteById(Long id, Long userId){
        if(!deliveryRepository.existsById(id)) {
            log.warn("attempt to delete nonexistent object");
            throw new NotFoundException();
        }
        Delivery deletingDelivery = deliveryRepository.getOne(id);
        actionService.captureDelete(deletingDelivery, "deliveries", userId);
        deliveryRepository.deleteById(id);
    }

    public Page<Delivery> findFiltered(Pageable pageable){
        return deliveryRepository.findAllByDeletedFalse(pageable);
    }

    public void setLoadIdToNextDeliveries(List<Long> deliveryIds, Long loadId, Long userId){
        for(Long id: deliveryIds){
            Delivery delivery = deliveryRepository.getOne(id);
            Delivery oldDelivery = delivery;
            delivery.setLoadId(loadId);
            actionService.captureUpdate(oldDelivery, delivery, "deliveries", userId);
            deliveryRepository.save(delivery);
        }
    }

    public List<Delivery> findAllForUpdate(Date currentDate, Boolean active, Boolean completed){
        return deliveryRepository.findAllByDeliveryDateAfterAndActiveAndCompletedAndLoadIdNotNull(currentDate, active, completed);
    }

    public List<Delivery> findAllForUpdateDelivered(Date currentDate, Boolean active, Boolean completed){
        return deliveryRepository.findAllByDeliveryDateBeforeAndActiveAndCompletedAndLoadIdNotNull(currentDate, active, completed);
    }

    public void update(Delivery delivery, Long userId) {
        Delivery oldVersion = deliveryRepository.getOne(delivery.getId());
        actionService.captureUpdate(oldVersion, delivery, "deliveries", userId);
        deliveryRepository.save(delivery);
    }

    public void update_(Delivery delivery){
        deliveryRepository.save(delivery);
    }

    public Boolean existsById(Long id){
        return deliveryRepository.existsById(id);
    }

    public Long minDeliveryTime(List<Long> deliveryIds){

        Long currentMin = Long.MAX_VALUE;

        List<Delivery> deliveries = deliveryRepository.findAllByIdIn(deliveryIds);

        for(Delivery delivery: deliveries){
            if(delivery.getDeliveryDate().getTime() < currentMin){
                currentMin = delivery.getDeliveryDate().getTime();
            }
        }

        return currentMin;
    }

    public Long maxDeliveryTime(List<Long> deliveryIds){
        Long currentMax = Long.MIN_VALUE;

        List<Delivery> deliveries = deliveryRepository.findAllByIdIn(deliveryIds);

        for(Delivery delivery: deliveries){
            if(delivery.getDeliveryDate().getTime() > currentMax){
                currentMax = delivery.getDeliveryDate().getTime();
            }
        }

        return currentMax;
    }

    public void addFileToDriversUploads(Long fileInfoId, Long deliveryId){

        Delivery delivery = deliveryRepository.getOne(deliveryId);

        if(delivery.getDriverUploads() == null){
            delivery.setDriverUploads(new ArrayList<>());
        }

        delivery.getDriverUploads().add(fileInfoId);
        deliveryRepository.save(delivery);

    }

    public List<Map<String, Object>> getUploadsWithIdsAndNames(Long pickupId){

        Delivery delivery = deliveryRepository.getOne(pickupId);

        List<Long> driverUploads = delivery.getDriverUploads();
        if(driverUploads == null){
            return new ArrayList<>();
        }

        List<Map<String, Object>> data = new ArrayList<>(driverUploads.size());

        for(Long fileId: driverUploads){
            Optional<FileInformation> fileInformationOptional = fileInformationService.getById(fileId);
            if(fileInformationOptional.isEmpty())
                continue;
            Map<String, Object> map = new HashMap<>();

            FileInformation fileInformation = fileInformationOptional.get();
            map.put("id", fileInformation.getId());
            map.put("original_filename", fileInformation.getOriginalFileName());

            data.add(map);
        }

        return data;
    }

    public void setDriversDeliveryTime(Long deliveryId, Long driversDeliveryTime){
        Delivery delivery = deliveryRepository.getOne(deliveryId);
        delivery.setDriversDeliveryTime(driversDeliveryTime);
        deliveryRepository.save(delivery);
    }

    public List<Long> getSortedByTimeAsc(List<Long> ids){
        return deliveryRepository.getSortedByTime(ids);
    }

}
