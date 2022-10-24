package com.dpontespro.devops.entities;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
class DpsProDemoTest {
    private DpsProDemo dpsProDemo1;
    private DpsProDemo dpsProDemo2;

    private final Date dateBefore = Timestamp.valueOf("2020-06-13 23:59:00");
    private final Date dateBetween = Timestamp.valueOf("2020-06-15 00:00:00");
    private final Date dateAfter = Timestamp.valueOf("2021-01-01 00:00:00");

    @BeforeEach
    void setUp() {
        this.dpsProDemo1 = DpsProDemo.builder().id(1L).brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).dpsProDemoList(1)
                .productId(35455).priority(0).dpsProDemo(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        this.dpsProDemo2 = DpsProDemo.builder().id(1L).brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).dpsProDemoList(1)
                .productId(35455).priority(0).dpsProDemo(BigDecimal.valueOf(35.50))
                .curr("EUR").build();

        new DpsProDemo();
    }

    @Test
    void testGetters() {
        log.info("===>>> getter:" + this.dpsProDemo1.getId());
        log.info("===>>> getter:" + this.dpsProDemo1.getBrandId());
        log.info("===>>> getter:" + this.dpsProDemo1.getStartDate());
        log.info("===>>> getter:" + this.dpsProDemo1.getEndDate());
        log.info("===>>> getter:" + this.dpsProDemo1.getDpsProDemoList());
        log.info("===>>> getter:" + this.dpsProDemo1.getProductId());
        log.info("===>>> getter:" + this.dpsProDemo1.getPriority());
        log.info("===>>> getter:" + this.dpsProDemo1.getDpsProDemo());
        log.info("===>>> getter:" + this.dpsProDemo1.getCurr());

    }

    @Test
    void testToString() {
        log.info("===>>> toString:" + this.dpsProDemo1.toString());
    }

    @Test
    void testEquals() {
        assertEquals(dpsProDemo1, dpsProDemo2);
    }


    @Test
    void testHashCode() {
        log.info("===>>> hashCode:" + this.dpsProDemo1.hashCode());
    }

    @Test
    @DisplayName("test_if_the_filter_date_is_between_the_dpsProDemo_range_days")
    void validDpsProDemoRangeTest() {


        //given a dpsProDemo1 range dates Then expect true
        Assertions.assertTrue(this.dpsProDemo1
                .validDpsProDemoRange(dateBetween), "testDate= " + dateBetween
                + "dpsProDemo1 startDate= " + this.dpsProDemo1.getStartDate()
                + " and endDate= " + this.dpsProDemo1.getEndDate());
    }

    @Test
    @DisplayName("test_if_the_filter_date_is_not_between_the_dpsProDemo_range_days")
    void testInvalidDpsProDemoRange() {
        //given a dpsProDemo1 range dates then expect false
        Assertions.assertAll(
                () -> Assertions.assertFalse(this.dpsProDemo1.validDpsProDemoRange(dateBefore),"testDate= " + dateBefore+ " dpsProDemo1 startDate= " + this.dpsProDemo1.getStartDate()),
                () -> Assertions.assertFalse(this.dpsProDemo1.validDpsProDemoRange(dateAfter), "testDate= " +dateAfter +" dpsProDemo1 endDate= " + this.dpsProDemo1.getEndDate()));


    }


    @Test
    @DisplayName("test_the_remaining_dpsProDemo_application_days_list")
    void lookForApplicationDates() {
        Assertions.assertTrue(this.dpsProDemo1.lookForApplicationDates(dateBetween).size()>1);
    }

    @Test
    void deleteInBatch() {
    }
}