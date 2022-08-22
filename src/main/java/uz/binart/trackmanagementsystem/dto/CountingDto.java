package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

@Data
public class CountingDto {

    private Long loadId;

    private Float booked = 0F;

    private Float dispute = 0F;

    private Float detention = 0F;

    private Float additional = 0F;

    private Float fine = 0F;

    private Float revisedInvoice;

    private Float factoring = 0F;

    private Float tafs = 0F;

    private Float netPaid;

}
