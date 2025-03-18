package kg.alatoo.e_commerce.controller;

import kg.alatoo.e_commerce.dto.user.ChangePasswordRequest;
import kg.alatoo.e_commerce.dto.user.WorkerInfoResponse;
import kg.alatoo.e_commerce.service.WorkerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/worker")
public class WorkerController {
    private final WorkerService workerService;

    @GetMapping("/info")
    public WorkerInfoResponse workerProfile(@RequestHeader("Authorization") String token){
        return workerService.workerInfo(token);
    }

    @PutMapping("/update")
    public String update(@RequestHeader("Authorization") String token, @RequestBody WorkerInfoResponse request){
        workerService.update(token, request);
        return "Profile updated.";
    }

    @PutMapping("/change_password")
    public String changePassword(@RequestHeader("Authorization") String token, @RequestBody ChangePasswordRequest request){
        workerService.changePassword(token, request);
        return "Password successfully changed.";
    }
}//+
