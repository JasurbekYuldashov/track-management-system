package uz.binart.trackmanagementsystem.service.impl.type;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.type.PaymentType;
import uz.binart.trackmanagementsystem.repository.type.PaymentTypeRepository;
import uz.binart.trackmanagementsystem.service.type.PaymentTypeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentTypeServiceImpl implements PaymentTypeService {
    private final PaymentTypeRepository paymentTypeRepository;

    public PaymentType save(String paymentTypeName, Long userId){
        return null;
    }

    public String getPaymentTypeName(Long paymentTypeId){

        if(!paymentTypeRepository.existsById(paymentTypeId))
            return "";

        PaymentType paymentType = paymentTypeRepository.getOne(paymentTypeId);

        return paymentType.getName();
    }

    public List<PaymentType> getAll(){
        return paymentTypeRepository.findAll();
    }


}
