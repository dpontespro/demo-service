package com.dpontespro.devops.controllers;

import com.dpontespro.devops.dtos.DpsProDemoRequestDto;
import com.dpontespro.devops.dtos.DpsProDemoResponseDto;
import com.dpontespro.devops.entities.DpsProDemo;
import com.dpontespro.devops.services.DpsProDemoAsyncService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@WebMvcTest(DpsProDemoController.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DpsProDemoControllerTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(DpsProDemoControllerTest.class);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private DpsProDemo dpsProDemo;
    private List<LocalDate> dpsProDemoDateList;
    private DpsProDemoRequestDto dpsProDemoRequestDto;
    private DpsProDemoResponseDto dpsProDemoResponseDto;

    @MockBean
    private DpsProDemoAsyncService dpsProDemoAsyncService;

    @MockBean
    private DpsProDemoController dpsProDemoController;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        dpsProDemoAsyncService = mock(DpsProDemoAsyncService.class);
        this.dpsProDemoController = new DpsProDemoController(dpsProDemoAsyncService);
        this.dpsProDemoRequestDto = DpsProDemoRequestDto.builder().requestDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .productId(35455).brandId(1L).build();
        this.dpsProDemoDateList = new ArrayList<>();
        this.dpsProDemoDateList.add(LocalDate.ofInstant(this.dpsProDemoRequestDto.getRequestDate().toInstant(), ZoneId.of("UTC")));


    }

    @Test
    void getDpsProDemoAsync() throws Exception {
        this.dpsProDemo = DpsProDemo.builder().brandId(1L)
                .startDate(Timestamp.valueOf("2020-06-14 00:00:00"))
                .endDate(Timestamp.valueOf("2020-12-31 23:59:59")).dpsProDemoList(1)
                .productId(35455).priority(0).dpsProDemo(BigDecimal.valueOf(35.50))
                .curr("EUR").build();
        this.dpsProDemoResponseDto = DpsProDemoResponseDto.builder()
                .productId(dpsProDemo.getProductId())
                .brandId(dpsProDemo.getBrandId())
                .dpsProDemoList(dpsProDemo.getDpsProDemoList())
                .applicationDates(dpsProDemoDateList)
                .dpsProDemo(dpsProDemo.getDpsProDemo()).build();
        String hour = "2020-06-14 00:00:00";
        Integer pId = 35455;
        String bId = "1";
        when(dpsProDemoAsyncService.getCurrentDpsProDemoByProductIdAndBrandId(dpsProDemoRequestDto))
                .thenReturn(new AsyncResult<>(this.dpsProDemoResponseDto));
        mockMvc.perform(get("/dpsProDemo/{hour},{productId},{brandId}", hour, pId, bId).contentType("application/json")
                ).andDo(print()).
                andExpect(status().isOk());
        Future<DpsProDemoResponseDto> responseDtoResult = dpsProDemoAsyncService.getCurrentDpsProDemoByProductIdAndBrandId(dpsProDemoRequestDto);

        Assertions.assertEquals(responseDtoResult.get().getDpsProDemo(), BigDecimal.valueOf(35.50));
        Assertions.assertEquals(responseDtoResult.get().getProductId(), 35455);
        Assertions.assertEquals(responseDtoResult.get().getBrandId(), 1L);
        Assertions.assertEquals(responseDtoResult.get().getApplicationDates().size(), 1);

    }

    @Test
    void getDpsProDemoAsyncBadRequest() throws Exception {
        String hour = "2020-06_14 00:00:00";
        Integer pId = 15;
        String bId = "1";
        this.dpsProDemo = null;
        when(dpsProDemoAsyncService.getCurrentDpsProDemoByProductIdAndBrandId(null))
                .thenReturn(new AsyncResult<>(null));
        mockMvc.perform(get("/dpsProDemo/{hour},{productId},{brandId}", hour, pId, bId)
                .contentType("application/json")
            ).andDo(print()).andExpect(status().isBadRequest());
        Future<DpsProDemoResponseDto> responseDtoResult = dpsProDemoAsyncService.getCurrentDpsProDemoByProductIdAndBrandId(dpsProDemoRequestDto);
        Assertions.assertNull(responseDtoResult);


    }

    @Test
    void deleteInBatch() {

    }


}