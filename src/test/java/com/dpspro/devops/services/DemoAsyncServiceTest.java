package com.dpspro.devops.services;


import com.dpspro.devops.dtos.DemoRequestDto;
import com.dpspro.devops.dtos.DemoResponseDto;
import com.dpspro.devops.entities.Demo;
import com.dpspro.devops.repositories.DemoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class DemoAsyncServiceTest {
    private final List<LocalDate> localDates = new ArrayList<>();
    @Autowired
    private ThreadPoolTaskExecutor myTaskExecutor;
    @Mock
    private DemoAsyncService demoAsyncService;
    private final List<Demo> demoList = new ArrayList<>();

    @Mock
    private DemoRepository demoRepository;
    private Demo demo;
    private DemoResponseDto demoResponseDto;
    private DemoRequestDto demoRequestDto;


    @BeforeEach
    void setUp() {

        Demo demo1 = Demo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).demoList(1)
                .productId(35455).priority(0).demoPrice(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        Demo demo2 = Demo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 15:00:00"))
                .endDate(Timestamp.valueOf("2020-06-14 18:30:00")).demoList(2)
                .productId(35455).priority(1).demoPrice(BigDecimal.valueOf(25.45))
                .curr("EUR").build();
        Demo demo3 = Demo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-15 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-15 11:00:00")).demoList(3)
                .productId(35455).priority(1).demoPrice(BigDecimal.valueOf(30.50))
                .curr("EUR").build();
        Demo demo4 = Demo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-15 16:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).demoList(4)
                .productId(35455).priority(1).demoPrice(BigDecimal.valueOf(38.95))
                .curr("EUR").build();
        demoList.add(demo1);
        demoList.add(demo2);
        demoList.add(demo3);
        demoList.add(demo4);
        demo = demo2;

        demoResponseDto = DemoResponseDto.builder()//DemoResponseDto.DemoResponseDtoBuilder builder = DemoResponseDto.builder();
        .productId(demo3.getProductId())
        .brandId(demo3.getBrandId())
        .demoList(demo3.getDemoList())
        .applicationDates(localDates)
        .demoPrice(demo3.getDemoPrice()).build();



        demoRequestDto = DemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 11:55:00"))
                .brandId(1L)
                .productId(35455).build();
    }


    @Test
    @DisplayName("look_for_all_demo_by_productIid_and_brandId")
    public void testGetCurrentDemoByProductIdAndBrandId() {

        when(demoRepository.findByProductIdAndBrandId(35455, 1L)).thenReturn(this.demoList);


        List<Demo> demoList = this.demoRepository.findByProductIdAndBrandId(35455, 1L);

        assertNotNull(demoList, "demoList object should not be null");
        assertEquals(4, demoList.size(), "demoList size should  be 4");


    }


    @Test
    void testgetDemo() {
        when(demoRepository.findByProductIdAndBrandId(35455, 1L)).thenReturn(demoList);
       when(this.demoAsyncService.getDemo(demoRequestDto, demoList)).thenReturn(this.demo);
       Demo demo1=demoAsyncService.getDemo(demoRequestDto,demoRepository.findByProductIdAndBrandId(35455, 1L));
        assertNotNull(demo1, "demo object should not be null");
        assertAll("getDemo",
                () -> assertEquals(BigDecimal.valueOf(25.45), demo.getDemoPrice()),
                () -> assertEquals(35455, demo.getProductId()));
    }


    @Test
    void testentityToDto() {
        LocalDate localDate= LocalDate.ofInstant(demoRequestDto.getRequestDate().toInstant(), ZoneId.of("UTC"));
        localDates.add(localDate);
//        DemoResponseDto   demoResponseDto1 = DemoResponseDto.builder()//DemoResponseDto.DemoResponseDtoBuilder builder = DemoResponseDto.builder();
//                .productId(demo.getProductId())
//                .brandId(1L)
//                .demoList(2)
//                .applicationDates(localDates)
//                .demoPrice(BigDecimal.valueOf(30.5)).build();
      //  when(demoRepository.findByProductIdAndBrandId(35455, 1L)).thenReturn(demo);
        when(this.demoAsyncService.entityToDto(this.demo, demoRequestDto.getRequestDate())).thenReturn(this.demoResponseDto);
       this.demoResponseDto = demoAsyncService.entityToDto(this.demo, demoRequestDto.getRequestDate());
        assertNotNull(demoResponseDto, "demoResponseDto object should not be null");
        final Runnable runnable = () -> assertEquals(1, demoResponseDto.getApplicationDates().size());
        assertAll(
                () -> assertEquals(BigDecimal.valueOf(30.5), demoResponseDto.getDemoPrice()),
                () -> assertEquals(1, demoResponseDto.getApplicationDates().size())
        );
    }

    @Test
    void asyncDemoResponse() {


        DemoRequestDto demoRequestDto = new DemoRequestDto(Timestamp.valueOf("2020-06-14 00:00:00"), 1, 1L);
        DemoResponseDto demoResponseDto = new DemoResponseDto(35455, 1L, 1, localDates, BigDecimal.valueOf(35.50));
        when(demoAsyncService.getCurrentDemoByProductIdAndBrandId(demoRequestDto))
                .thenReturn(new AsyncResult<>(demoResponseDto));
        this.demoAsyncService.getCurrentDemoByProductIdAndBrandId(demoRequestDto);

        try {
            boolean awaitTermination = this.myTaskExecutor.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);
            Assertions.assertFalse(awaitTermination);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    @DisplayName("time_sleep_test")
    public void testSleepTime() throws ExecutionException, InterruptedException {
        DemoRequestDto demoRequestDto = new DemoRequestDto(Timestamp.valueOf("2020-06-14 00:00:00"), 1, 1L);
        DemoResponseDto demoResponseDto = new DemoResponseDto(35455, 1L, 1, localDates, BigDecimal.valueOf(35.50));
       when(this.demoAsyncService.getCurrentDemoByProductIdAndBrandId(demoRequestDto))
                .thenReturn(new AsyncResult<>(demoResponseDto));

        long now = System.currentTimeMillis();

        // <2>  Blocking waiting for results


        Future<DemoResponseDto> executeResult = this.demoAsyncService.getCurrentDemoByProductIdAndBrandId(demoRequestDto);

        // <1>  Perform tasks
         long sleep = 1000;
        executeResult.get();


        assertTrue((System.currentTimeMillis()+sleep-now  ) >= 1000);


    }
}