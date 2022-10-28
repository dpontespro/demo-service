package com.dpspro.devops;

import com.dpspro.devops.dtos.DemoRequestDto;
import com.dpspro.devops.dtos.DemoResponseDto;
import com.dpspro.devops.entities.Demo;
import com.dpspro.devops.repositories.DemoRepository;
import com.dpspro.devops.services.DemoAsyncService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ExpectedBehaviorIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DemoAsyncService demoService;
    @MockBean
    private DemoRepository demoRepository;
    Demo demo;
    private final List<LocalDate> localDates = new ArrayList<>();
    private final List<Demo> demoList = new ArrayList<>();

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

        DemoResponseDto demoResponseDto = DemoResponseDto.builder()
                .productId(demo3.getProductId())
                .brandId(demo3.getBrandId())
                .demoList(demo3.getDemoList())
                .applicationDates(localDates)
                .demoPrice(demo3.getDemoPrice()).build();

        DemoRequestDto demoRequestDto = DemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 11:55:00"))
                .brandId(1L)
                .productId(35455).build();

    }

    @ParameterizedTest
    @MethodSource("demoRequestDtoProviderFactory")
    void testAllDemo(DemoRequestDto demoRequestDtoAll) throws RuntimeException{

        when(demoRepository.findByProductIdAndBrandId(demoRequestDtoAll.getProductId(), demoRequestDtoAll.getBrandId())).thenReturn(demoList);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/demo/{hour},{productId},{brandId}", demoRequestDtoAll.getRequestDate()
                                    , demoRequestDtoAll.getProductId(), demoRequestDtoAll.getBrandId()
                            )
                            .contentType("application/json")
            ).andDo(print()).andExpect(status().isOk());
            DemoResponseDto responseDtoResult = demoService.getCurrentDemoByProductIdAndBrandId(demoRequestDtoAll).get();
            Assertions.assertAll(
                    () -> assertEquals(35455, responseDtoResult.getProductId()
                            , "ProductIOd " + responseDtoResult.getProductId()),
                    () -> assertEquals(1L, responseDtoResult.getBrandId()
                            , "branddD " + responseDtoResult.getBrandId()),
                    () -> assertTrue(responseDtoResult.getApplicationDates().size() > 0
                            , "mandatory aplication days" + responseDtoResult.getApplicationDates().size()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    static Iterator<DemoRequestDto> demoRequestDtoProviderFactory() {
        List<DemoRequestDto> requestDtos = new ArrayList<>();
        requestDtos.add(DemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 10:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(DemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 16:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(DemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 21:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(DemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-15 10:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(DemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-16 21:00:00"))
                .productId(35455)
                .brandId(1L).build());
        return requestDtos.stream().iterator();
    }
    @Test
    void deleteInBatch() {
    }
}
