package uz.binart.trackmanagementsystem.service;

import org.springframework.boot.configurationprocessor.json.JSONException;

public interface ScheduleService {

    void updateTripAndUnitStatuses() throws JSONException;

    void trackExpirationOfDriversDocuments();

    void trackExpirationOfUnitDocuments();

}
