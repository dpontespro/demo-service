package com.dpontespro.devops.repositories;

import com.dpontespro.devops.entities.DpsProDemo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DpsProDemoRepository extends JpaRepository<DpsProDemo, Long> {
    List<DpsProDemo> findByProductIdAndBrandId(Integer productId,
                                          Long brandId);
}