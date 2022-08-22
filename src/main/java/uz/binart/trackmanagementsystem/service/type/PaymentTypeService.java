package uz.binart.trackmanagementsystem.service.type;

import uz.binart.trackmanagementsystem.model.type.PaymentType;

import java.util.List;

public interface PaymentTypeService {
    PaymentType save(String paymentTypeName, Long userId);

    String getPaymentTypeName(Long paymentTypeId);

    List<PaymentType> getAll();
}
