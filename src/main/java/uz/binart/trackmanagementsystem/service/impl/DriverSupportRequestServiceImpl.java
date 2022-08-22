package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.DriverSupportRequest;
import uz.binart.trackmanagementsystem.repository.DriverSupportRequestRepository;
import uz.binart.trackmanagementsystem.service.DriverSupportRequestService;

@Service
@RequiredArgsConstructor
public class DriverSupportRequestServiceImpl implements DriverSupportRequestService {

    private final DriverSupportRequestRepository driverSupportRequestRepository;

    public DriverSupportRequest save(DriverSupportRequest driverSupportRequest){
        return driverSupportRequestRepository.save(driverSupportRequest);
    }

}
