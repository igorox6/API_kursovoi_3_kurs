package com.example.api_1.controller;

import com.example.api_1.entity.User;
import com.example.api_1.entity.Worker;
import com.example.api_1.pojo.WorkerBody;
import com.example.api_1.repo.UserRepository;
import com.example.api_1.repo.WorkerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workers")
public class WorkerController {
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    public WorkerController(WorkerRepository workerRepository, UserRepository userRepository) {
        this.workerRepository = workerRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Worker>> getWorkers() {
        return ResponseEntity.ok(workerRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Worker> addWorker(@RequestBody WorkerBody body) {
        Worker worker = new Worker();
        worker.setId_position(body.getId_position());
        worker.setName(body.getName());
        worker.setLastname(body.getLastname());

        User user = userRepository.findById(body.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + body.getUserId()));

        worker.setUser(user);

        workerRepository.save(worker);
        return ResponseEntity.ok(worker);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWorker(@PathVariable Long id) {
        workerRepository.deleteById(id);
        return ResponseEntity.ok("Worker deleted");
    }
}
