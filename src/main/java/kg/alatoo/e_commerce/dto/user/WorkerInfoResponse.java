package kg.alatoo.e_commerce.dto.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class WorkerInfoResponse {
    private Long id;
    private String username;
    private String email;

}
