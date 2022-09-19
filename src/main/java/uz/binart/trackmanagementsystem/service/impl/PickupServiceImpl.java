package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.exception.IllegalChangeAttemptException;
import uz.binart.trackmanagementsystem.exception.NotFoundException;
import uz.binart.trackmanagementsystem.exception.WrongEntityStructureException;
import uz.binart.trackmanagementsystem.model.FileInformation;
import uz.binart.trackmanagementsystem.model.Pickup;
import uz.binart.trackmanagementsystem.repository.PickupRepository;
import uz.binart.trackmanagementsystem.service.*;
import uz.binart.trackmanagementsystem.service.type.QuantityTypeService;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PickupServiceImpl implements PickupService {

    private final PickupRepository pickupRepository;
    private final QuantityTypeService quantityTypeService;
    private final ActionService actionService;
    private final SequenceService sequenceService;
    private final FileInformationService fileINformationService;

    public Pickup save(Pickup pickup, Long userId){
        if(pickup.getId() != null && pickupRepository.existsById(pickup.getId())){
            log.warn("trying to overwrite an existing object in 'pickups' table");
            throw new IllegalChangeAttemptException("trying to overwrite an existing object in 'pickups' table");
        }
        if(pickup.getQuantityTypeId() != null && !quantityTypeService.existsById(pickup.getQuantityTypeId())){
            log.warn("such quantity type does not exist");
            throw new WrongEntityStructureException("no such quantity type");
        }
       // sequenceService.updateSequence("pickups", "deliveries", "pickups_and_deliveries_id_seq");
        Pickup savedPickup = pickupRepository.save(pickup);
        actionService.captureCreate(savedPickup, "pickups", userId);
        return savedPickup;
    }

    public void deleteById(Long id, Long userId){
        if(!pickupRepository.existsById(id)){
            log.info("attempt to delete nonexistent object");
            throw new NotFoundException();
        }
        Pickup deletingPickup = pickupRepository.getOne(id);
        deletingPickup.setDeleted(true);
        actionService.captureDelete(deletingPickup, "pickups", userId);
        pickupRepository.save(deletingPickup);
    }

    public Page<Pickup> findFiltered(Pageable pageable){
        return pickupRepository.findAll(pageable);
    }

    public Pickup getById(Long id){
        Optional<Pickup> pickupOpt = pickupRepository.findById(id);
        if(pickupOpt.isEmpty()){
            log.info("attempt to get nonexistent object");
            throw new NotFoundException();
        }else return pickupOpt.get();
    }

    public void saveLoadIdToNextPickups(List<Long> pickupIds, Long loadId){
        for(Long id: pickupIds){
            Pickup pickup = pickupRepository.getOne(id);
            pickup.setLoadId(loadId);
            pickupRepository.save(pickup);
        }
    }

    public List<Pickup> findAllForUpdateBefore(Date currentDate, Boolean active, Boolean completed){
        return pickupRepository.findAllByPickupDateBeforeAndActiveAndCompletedAndLoadIdNotNull(currentDate, active, completed);
    }

    public List<Pickup> findAllForUpdateAfter(Date currentDate, Boolean active, Boolean completed){
        return pickupRepository.findAllByPickupDateAfterAndActiveAndCompletedAndLoadIdNotNull(currentDate, active, completed);
    }

    public void update(Pickup pickup, Long userId){
        Pickup oldPickup = pickupRepository.getOne(pickup.getId());
        actionService.captureUpdate(oldPickup, pickup, "pickups", userId);
        pickupRepository.save(pickup);
    }

    public Boolean existsById(Long id){
        return pickupRepository.existsById(id);
    }

    public Long minPickupTime(List<Long> pickupIds){
        long currentMin = Long.MAX_VALUE;
        List<Pickup> pickups = pickupRepository.findAllByIdIn(pickupIds);

        for(Pickup pickup: pickups){
            if(pickup.getPickupDate().getTime() < currentMin){
                currentMin = pickup.getPickupDate().getTime();
            }
        }
        System.out.println(currentMin);

        return currentMin;
    }

    public Long maxPickupTime(List<Long> pickupIds){
        long currentMax = Long.MIN_VALUE;
        List<Pickup> pickups = pickupRepository.findAllByIdIn(pickupIds);


        for(Pickup pickup: pickups){
            if(pickup.getPickupDate().getTime() > currentMax){
                currentMax = pickup.getPickupDate().getTime();
            }
        }
        System.out.println(currentMax);

        return currentMax;
    }

    public void addFileToDriversUploads(Long fileInfoId, Long pickupId){

        Pickup pickup = pickupRepository.getOne(pickupId);

        if(pickup.getDriverUploads() == null){
            pickup.setDriverUploads(new ArrayList<>());
        }

        pickup.getDriverUploads().add(fileInfoId);
        pickupRepository.save(pickup);
    }

    public List<Map<String, Object>> getUploadsWithIdsAndNames(Long pickupId){

        Pickup pickup = pickupRepository.getOne(pickupId);

        List<Long> driverUploads = pickup.getDriverUploads();
        if(driverUploads == null){
            return new ArrayList<>();
        }

        List<Map<String, Object>> data = new ArrayList<>(driverUploads.size());

        for(Long fileId: driverUploads){
            Optional<FileInformation> fileInformationOptional = fileINformationService.getById(fileId);
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

    public void setDriversPickupTime(Long pickupId, Long driversPickupTime){
        Pickup pickup = pickupRepository.getOne(pickupId);
        pickup.setDriversPickupTime(driversPickupTime);
        pickupRepository.save(pickup);
    }

    public List<Long> getSortedByTimeAsc(List<Long> pickups){
        return pickupRepository.getSortedByTime(pickups);
    }

}
