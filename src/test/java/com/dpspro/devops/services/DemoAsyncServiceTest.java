package com.dpontespro.devops.services;


import com.dpontespro.devops.dtos.DpsProDemoRequestDto;
import com.dpontespro.devops.dtos.DpsProDemoResponseDto;
import com.dpontespro.devops.entities.DpsProDemo;
import com.dpontespro.devops.repositories.DpsProDemoRepository;
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
class DpsProDemoAsyncServiceTest {
    private final List<LocalDate> localDates = new ArrayList<>();
    @Autowired
    private ThreadPoolTaskExecutor myTaskExecutor;
    @Mock
    private DpsProDemoAsyncService dpsProDemoAsyncService;
    private final List<DpsProDemo> dpsProDemoList = new ArrayList<>();

    @Mock
    private DpsProDemoRepository dpsProDemoRepository;
    private DpsProDemo dpsProDemo;
    private DpsProDemoResponseDto dpsProDemoResponseDto;
    private DpsProDemoRequestDto dpsProDemoRequestDto;


    @BeforeEach
    void setUp() {

        DpsProDemo dpsProDemo1 = DpsProDemo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).dpsProDemoList(1)
                .productId(35455).priority(0).dpsProDemo(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        DpsProDemo dpsProDemo2 = DpsProDemo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 15:00:00"))
                .endDate(Timestamp.valueOf("2020-06-14 18:30:00")).dpsProDemoList(2)
                .productId(35455).priority(1).dpsProDemo(BigDecimal.valueOf(25.45))
                .curr("EUR").build();
        DpsProDemo dpsProDemo3 = DpsProDemo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-15 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-15 11:00:00")).dpsProDemoList(3)
                .productId(35455).priority(1).dpsProDemo(BigDecimal.valueOf(30.50))
                .curr("EUR").build();
        DpsProDemo dpsProDemo4 = DpsProDemo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-15 16:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).dpsProDemoList(4)
                .productId(35455).priority(1).dpsProDemo(BigDecimal.valueOf(38.95))
                .curr("EUR").build();
        dpsProDemoList.add(dpsProDemo1);
        dpsProDemoList.add(dpsProDemo2);
        dpsProDemoList.add(dpsProDemo3);
        dpsProDemoList.add(dpsProDemo4);
        dpsProDemo = dpsProDemo2;

        dpsProDemoResponseDto = DpsProDemoResponseDto.builder()//DpsProDemoResponseDto.DpsProDemoResponseDtoBuilder builder = DpsProDemoResponseDto.builder();
        .productId(dpsProDemo3.getProductId())
        .brandId(dpsProDemo3.getBrandId())
        .dpsProDemoList(dpsProDemo3.getDpsProDemoList())
        .applicationDates(localDates)
        .dpsProDemo(dpsProDemo3.getDpsProDemo()).build();



        dpsProDemoRequestDto = DpsProDemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 11:55:00"))
                .brandId(1L)
                .productId(35455).build();
    }


    @Test
    @DisplayName("look_for_all_dpsProDemo_by_productIid_and_brandId")
    public void testGetCurrentDpsProDemoByProductIdAndBrandId() {

        when(dpsProDemoRepository.findByProductIdAndBrandId(35455, 1L)).thenReturn(this.dpsProDemoList);


        List<DpsProDemo> dpsProDemoList = this.dpsProDemoRepository.findByProductIdAndBrandId(35455, 1L);

        assertNotNull(dpsProDemoList, "dpsProDemoList object should not be null");
        assertEquals(4, dpsProDemoList.size(), "dpsProDemoList size should  be 4");


    }


    @Test
    void testgetDpsProDemo() {
        when(dpsProDemoRepository.findByProductIdAndBrandId(35455, 1L)).thenReturn(dpsProDemoList);
       when(this.dpsProDemoAsyncService.getDpsProDemo(dpsProDemoRequestDto, dpsProDemoList)).thenReturn(this.dpsProDemo);
       DpsProDemo dpsProDemo1=dpsProDemoAsyncService.getDpsProDemo(dpsProDemoRequestDto,dpsProDemoRepository.findByProductIdAndBrandId(35455, 1L));
        assertNotNull(dpsProDemo1, "dpsProDemo object should not be null");
        assertAll("getDpsProDemo",
                () -> assertEquals(BigDecimal.valueOf(25.45), dpsProDemo.getDpsProDemo()),
                () -> assertEquals(35455, dpsProDemo.getProductId()));
    }


    @Test
    void testentityToDto() {
        LocalDate localDate= LocalDate.ofInstant(dpsProDemoRequestDto.getRequestDate().toInstant(), ZoneId.of("UTC"));
        localDates.add(localDate);
//        DpsProDemoResponseDto   dpsProDemoResponseDto1 = DpsProDemoResponseDto.builder()//DpsProDemoResponseDto.DpsProDemoResponseDtoBuilder builder = DpsProDemoResponseDto.builder();
//                .productId(dpsProDemo.getProductId())
//                .brandId(1L)
//                .dpsProDemoList(2)
//                .applicationDates(localDates)
//                .dpsProDemo(BigDecimal.valueOf(30.5)).build();
      //  when(dpsProDemoRepository.findByProductIdAndBrandId(35455, 1L)).thenReturn(dpsProDemo);
        when(this.dpsProDemoAsyncService.entityToDto(this.dpsProDemo, dpsProDemoRequestDto.getRequestDate())).thenReturn(this.dpsProDemoResponseDto);
       this.dpsProDemoResponseDto = dpsProDemoAsyncService.entityToDto(this.dpsProDemo, dpsProDemoRequestDto.getRequestDate());
        assertNotNull(dpsProDemoResponseDto, "dpsProDemoResponseDto object should not be null");
        final Runnable runnable = () -> assertEquals(1, dpsProDemoResponseDto.getApplicationDates().size());
        assertAll(
                () -> assertEquals(BigDecimal.valueOf(30.5), dpsProDemoResponseDto.getDpsProDemo()),
                () -> assertEquals(1, dpsProDemoResponseDto.getApplicationDates().size())
        );
    }

    @Test
    void asyncDpsProDemoResponse() {


        DpsProDemoRequestDto dpsProDemoRequestDto = new DpsProDemoRequestDto(Timestamp.valueOf("2020-06-14 00:00:00"), 1, 1L);
        DpsProDemoResponseDto dpsProDemoResponseDto = new DpsProDemoResponseDto(35455, 1L, 1, localDates, BigDecimal.valueOf(35.50));
        when(dpsProDemoAsyncService.getCurrentDpsProDemoByProductIdAndBrandId(dpsProDemoRequestDto))
                .thenReturn(new AsyncResult<>(dpsProDemoResponseDto));
        this.dpsProDemoAsyncService.getCurrentDpsProDemoByProductIdAndBrandId(dpsProDemoRequestDto);

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
        DpsProDemoRequestDto dpsProDemoRequestDto = new DpsProDemoRequestDto(Timestamp.valueOf("2020-06-14 00:00:00"), 1, 1L);
        DpsProDemoResponseDto dpsProDemoResponseDto = new DpsProDemoResponseDto(35455, 1L, 1, localDates, BigDecimal.valueOf(35.50));
       when(this.dpsProDemoAsyncService.getCurrentDpsProDemoByProductIdAndBrandId(dpsProDemoRequestDto))
                .thenReturn(new AsyncResult<>(dpsProDemoResponseDto));

        long now = System.currentTimeMillis();

        // <2>  Blocking waiting for results


        Future<DpsProDemoResponseDto> executeResult = this.dpsProDemoAsyncService.getCurrentDpsProDemoByProductIdAndBrandId(dpsProDemoRequestDto);

        // <1>  Perform tasks
         long sleep = 1000;
        executeResult.get();


        assertTrue((System.currentTimeMillis()+sleep-now  ) >= 1000);


    }
}