package com.dpspro.devops.controllers;

import com.dpspro.devops.dtos.DemoRequestDto;
import com.dpspro.devops.dtos.DemoResponseDto;
import com.dpspro.devops.entities.Demo;
import com.dpspro.devops.services.DemoAsyncService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@RunWith(SpringRunner.class)

@WebMvcTest(DemoController.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DemoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private Demo demo;
    private List<LocalDate> demoDateList;
    private DemoRequestDto demoRequestDto;
    private DemoResponseDto demoResponseDto;

    @MockBean
    private DemoAsyncService demoAsyncService;

    @MockBean
    private DemoController demoController;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        demoAsyncService = mock(DemoAsyncService.class);
        this.demoController = new DemoController(demoAsyncService);
        this.demoRequestDto = DemoRequestDto.builder().requestDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .productId(35455).brandId(1L).build();
        this.demoDateList = new ArrayList<>();
        this.demoDateList.add(LocalDate.ofInstant(this.demoRequestDto.getRequestDate().toInstant(), ZoneId.of("UTC")));


    }

    @Test
    void getDemoAsync() throws Exception {
        this.demo = Demo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).demoList(1)
                .productId(35455).priority(0).demoPrice(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        this.demoResponseDto = DemoResponseDto.builder()
                .productId(demo.getProductId())
                .brandId(demo.getBrandId())
                .demoList(demo.getDemoList())
                .applicationDates(demoDateList)
                .demoPrice(demo.getDemoPrice()).build();
        String hour = "2020-06-14 00:00:00";
        Integer pId = 35455;
        String bId = "1";
        when(demoAsyncService.getCurrentDemoByProductIdAndBrandId(demoRequestDto))
                .thenReturn(new AsyncResult<>(this.demoResponseDto));
        mockMvc.perform(get("/demo/{hour},{productId},{brandId}", hour, pId, bId).contentType("application/json")
                ).andDo(print()).
                andExpect(status().isOk());
        Future<DemoResponseDto> responseDtoResult = demoAsyncService.getCurrentDemoByProductIdAndBrandId(demoRequestDto);

        Assertions.assertEquals(responseDtoResult.get().getDemoPrice(), BigDecimal.valueOf(35.50));
        Assertions.assertEquals(responseDtoResult.get().getProductId(), 35455);
        Assertions.assertEquals(responseDtoResult.get().getBrandId(), 1L);
        Assertions.assertEquals(responseDtoResult.get().getApplicationDates().size(), 1);

    }

    @Test
    void getDemoAsyncBadRequest() throws Exception {
        String hour = "2020-06_14 00:00:00";
        Integer pId = 15;
        String bId = "1";
        this.demo = null;
        when(demoAsyncService.getCurrentDemoByProductIdAndBrandId(null))
                .thenReturn(new AsyncResult<>(null));
        mockMvc.perform(get("/demo/{hour},{productId},{brandId}", hour, pId, bId)
                .contentType("application/json")
        ).andDo(print()).andExpect(status().isBadRequest());
        Future<DemoResponseDto> responseDtoResult = demoAsyncService.getCurrentDemoByProductIdAndBrandId(demoRequestDto);
        Assertions.assertNull(responseDtoResult);


    }

    @Test
    void deleteInBatch() {

    }


}