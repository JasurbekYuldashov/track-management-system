package uz.binart.trackmanagementsystem.dto;

import lombok.Data;

@Data
public class PushNotificationRequest {
    private String title;
    private String message;
    private String token;
}
