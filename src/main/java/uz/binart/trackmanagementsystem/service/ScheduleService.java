package uz.binart.trackmanagementsystem.service;

public interface ScheduleService {

    void updateTripAndUnitStatuses();

    void trackExpirationOfDriversDocuments();

    void trackExpirationOfUnitDocuments();

}
