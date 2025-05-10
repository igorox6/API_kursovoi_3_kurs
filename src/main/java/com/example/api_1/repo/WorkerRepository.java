package com.example.api_1.repo;

import com.example.api_1.entity.Worker;
import org.springframework.data.repository.CrudRepository;

public interface WorkerRepository extends CrudRepository<Worker, Long> {
}
