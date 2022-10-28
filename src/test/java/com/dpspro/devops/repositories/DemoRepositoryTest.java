package com.dpspro.devops.repositories;

import com.dpspro.devops.entities.Demo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class DemoRepositoryTest {

    @Autowired
    private DemoRepository demoRepository;
    private Demo demo;

    @BeforeEach
    void setUp() {

        demo = Demo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2021-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2021-12-31 23:59:59")).demoList(5)
                .productId(99999).priority(1).demoPrice(BigDecimal.valueOf(60.60))
                .curr("EUR").build();
    }


    @Test
    @DisplayName("can_get_a_demo_by_productId_and_brandId")
    void findByProductIdAndBrandIdTest() {
        //given

        demo = Demo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2021-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2021-12-31 23:59:59")).demoList(5)
                .productId(99999).priority(1).demoPrice(BigDecimal.valueOf(160.60))
                .curr("EUR").build();

        Demo demoFromPersistence = demoRepository.save(demo);

        assertThat(demoRepository.findByProductIdAndBrandId(99999, 1L)).isNotNull();
        assertThat(demoRepository.findByProductIdAndBrandId(99999, 1L).get(0)
                .getDemoPrice()).isEqualTo(demoFromPersistence.getDemoPrice());

    }


    @Test
    void findByProductIdAndBrandId() {
        demo = new Demo(1L,
                Timestamp.valueOf("2021-06-14 00:00:00"),
                Timestamp.valueOf("2021-12-31 23:59:59"), 50
                , 35456, 1, BigDecimal.valueOf(60.60)
                , "EUR");

        demoRepository.save(demo);
        //then
        List<Demo> byProductIdAndBrandId;
        byProductIdAndBrandId = demoRepository
                .findByProductIdAndBrandId(35456, 1L);

        Assertions.assertEquals(byProductIdAndBrandId.size(), 1);
    }

    @Test
    void deleteInBatch() {
    }
}
