package uz.binart.trackmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AccountingDto {

    private Integer serialNumber;

    private Long number;

    private String carrierName;

    private String rc;

    private String company;

    private String shipperCompanyName;

    private Long timeStart;

    private String shipperCompanyLocation;

    private Long endTime;

    private String endLocation;

    private String truckNumber;

    private String truckCompany;

    private Float booked;

    private Float dispute;

    private Float detention;

    private Float additional;

    private Float fine;

    private Float revisedInvoice;

    private Float ko;
    @JsonProperty("amount")
    private Float factoring;
    @JsonProperty("service")
    private Float tafs;

    private Float netPaid;

    private String dateTime;

    private String team;

    private String note;

    private double[] segmentedPrices;

}
