package com.dpontespro.devops.repositories;

import com.dpontespro.devops.entities.DpsProDemo;
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
class DpsProDemoRepositoryTest {

    @Autowired
    private DpsProDemoRepository dpsProDemoRepository;
    private DpsProDemo dpsProDemo;

    @BeforeEach
    void setUp() {

        dpsProDemo = DpsProDemo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2021-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2021-12-31 23:59:59")).dpsProDemoList(5)
                .productId(99999).priority(1).dpsProDemo(BigDecimal.valueOf(60.60))
                .curr("EUR").build();
    }


    @Test
    @DisplayName("can_get_a_dpsProDemo_by_productId_and_brandId")
    void findByProductIdAndBrandIdTest() {
        //given

        dpsProDemo = DpsProDemo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2021-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2021-12-31 23:59:59")).dpsProDemoList(5)
                .productId(99999).priority(1).dpsProDemo(BigDecimal.valueOf(160.60))
                .curr("EUR").build();

        DpsProDemo dpsProDemoFromPersistence = dpsProDemoRepository.save(dpsProDemo);

        assertThat(dpsProDemoRepository.findByProductIdAndBrandId(99999, 1L)).isNotNull();
        assertThat(dpsProDemoRepository.findByProductIdAndBrandId(99999, 1L).get(0)
                .getDpsProDemo()).isEqualTo(dpsProDemoFromPersistence.getDpsProDemo());

    }


    @Test
    void findByProductIdAndBrandId() {
        dpsProDemo = new DpsProDemo(1L,
                Timestamp.valueOf("2021-06-14 00:00:00"),
                Timestamp.valueOf("2021-12-31 23:59:59"), 50
                , 35456, 1, BigDecimal.valueOf(60.60)
                , "EUR");

        dpsProDemoRepository.save(dpsProDemo);
        //then
        List<DpsProDemo> byProductIdAndBrandId;
        byProductIdAndBrandId = dpsProDemoRepository
                .findByProductIdAndBrandId(35456, 1L);

        Assertions.assertEquals(byProductIdAndBrandId.size(), 1);
    }

    @Test
    void deleteInBatch() {
    }
}
