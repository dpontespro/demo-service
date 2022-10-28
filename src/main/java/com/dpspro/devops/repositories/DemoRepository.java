package com.dpspro.devops.repositories;

import com.dpspro.devops.entities.Demo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemoRepository extends JpaRepository<Demo, Long> {
    List<Demo> findByProductIdAndBrandId(Integer productId,
                                          Long brandId);
}