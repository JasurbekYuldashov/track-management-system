package uz.binart.trackmanagementsystem.service;

import org.springframework.data.util.Pair;
import uz.binart.trackmanagementsystem.model.Company;

import java.util.Date;

public interface TimeService {

    Pair<Long, Long> getCorrectedByCentralTimeTimestamps(Date localStartTime, Date localEndTime, Company shipper, Company consignee);

}
