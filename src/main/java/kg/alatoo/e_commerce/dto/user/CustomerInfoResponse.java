package kg.alatoo.e_commerce.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerInfoResponse {
    private Long id;
    private String username;
    private String email;
    private Double balance;
    private String country;
    private String address;
    private String city;
    private String zipCode;
    private String phone;
    private String additionalInfo;

}
