package kg.alatoo.e_commerce.controller;

import kg.alatoo.e_commerce.dto.user.ChangePasswordRequest;
import kg.alatoo.e_commerce.dto.user.WorkerInfoResponse;
import kg.alatoo.e_commerce.service.WorkerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/worker")
public class WorkerController {

    private final WorkerService workerService;

    @GetMapping("/info")
    public ResponseEntity<WorkerInfoResponse> workerProfile(@RequestHeader("Authorization") String token){
        return ResponseEntity.ok(workerService.workerInfo(token));
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestHeader("Authorization") String token, @RequestBody WorkerInfoResponse request){
        workerService.update(token, request);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestHeader("Authorization") String token) {
        workerService.delete(token);
        return ResponseEntity.ok("Profile deleted successfully");
    }

}
