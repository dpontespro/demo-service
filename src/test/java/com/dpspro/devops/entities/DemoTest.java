package com.dpspro.devops.entities;

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
class DemoTest {
    private Demo demo1;
    private Demo demo2;

    private final Date dateBefore = Timestamp.valueOf("2020-06-13 23:59:00");
    private final Date dateBetween = Timestamp.valueOf("2020-06-15 00:00:00");
    private final Date dateAfter = Timestamp.valueOf("2021-01-01 00:00:00");

    @BeforeEach
    void setUp() {
        this.demo1 = Demo.builder().id(1L).brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).demoList(1)
                .productId(35455).priority(0).demoPrice(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        this.demo2 = Demo.builder().id(1L).brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).demoList(1)
                .productId(35455).priority(0).demoPrice(BigDecimal.valueOf(35.50))
                .curr("EUR").build();

        new Demo();
    }

    @Test
    void testGetters() {
        log.info("===>>> getter:" + this.demo1.getId());
        log.info("===>>> getter:" + this.demo1.getBrandId());
        log.info("===>>> getter:" + this.demo1.getStartDate());
        log.info("===>>> getter:" + this.demo1.getEndDate());
        log.info("===>>> getter:" + this.demo1.getDemoList());
        log.info("===>>> getter:" + this.demo1.getProductId());
        log.info("===>>> getter:" + this.demo1.getPriority());
        log.info("===>>> getter:" + this.demo1.getDemoPrice());
        log.info("===>>> getter:" + this.demo1.getCurr());

    }

    @Test
    void testToString() {
        log.info("===>>> toString:" + this.demo1.toString());
    }

    @Test
    void testEquals() {
        assertEquals(demo1, demo2);
    }


    @Test
    void testHashCode() {
        log.info("===>>> hashCode:" + this.demo1.hashCode());
    }

    @Test
    @DisplayName("test_if_the_filter_date_is_between_the_demo_range_days")
    void validDemoRangeTest() {


        //given a demo1 range dates Then expect true
        Assertions.assertTrue(this.demo1
                .validDemoRange(dateBetween), "testDate= " + dateBetween
                + "demo1 startDate= " + this.demo1.getStartDate()
                + " and endDate= " + this.demo1.getEndDate());
    }

    @Test
    @DisplayName("test_if_the_filter_date_is_not_between_the_demo_range_days")
    void testInvalidDemoRange() {
        //given a demo1 range dates then expect false
        Assertions.assertAll(
                () -> Assertions.assertFalse(this.demo1.validDemoRange(dateBefore),"testDate= " + dateBefore+ " demo1 startDate= " + this.demo1.getStartDate()),
                () -> Assertions.assertFalse(this.demo1.validDemoRange(dateAfter), "testDate= " +dateAfter +" demo1 endDate= " + this.demo1.getEndDate()));


    }


    @Test
    @DisplayName("test_the_remaining_demo_application_days_list")
    void lookForApplicationDates() {
        Assertions.assertTrue(this.demo1.lookForApplicationDates(dateBetween).size()>1);
    }

    @Test
    void deleteInBatch() {
    }
}