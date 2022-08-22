package uz.binart.trackmanagementsystem.dto.excel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
}
