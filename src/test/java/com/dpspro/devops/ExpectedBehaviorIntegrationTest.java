package com.dpontespro.devops;

import com.dpontespro.devops.dtos.DpsProDemoRequestDto;
import com.dpontespro.devops.dtos.DpsProDemoResponseDto;
import com.dpontespro.devops.entities.DpsProDemo;
import com.dpontespro.devops.repositories.DpsProDemoRepository;
import com.dpontespro.devops.services.DpsProDemoAsyncService;
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
    private DpsProDemoAsyncService dpsProDemoService;
    @MockBean
    private DpsProDemoRepository dpsProDemoRepository;
    DpsProDemo dpsProDemo;
    private final List<LocalDate> localDates = new ArrayList<>();
    private final List<DpsProDemo> dpsProDemoList = new ArrayList<>();
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

        dpsProDemoResponseDto = DpsProDemoResponseDto.builder()
                .productId(dpsProDemo3.getProductId())
                .brandId(dpsProDemo3.getBrandId())
                .dpsProDemoList(dpsProDemo3.getDpsProDemoList())
                .applicationDates(new ArrayList<LocalDate>())
                .dpsProDemo(dpsProDemo3.getDpsProDemo()).build();

        dpsProDemoRequestDto = DpsProDemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 11:55:00"))
                .brandId(1L)
                .productId(35455).build();

    }

    @ParameterizedTest
    @MethodSource("dpsProDemoRequestDtoProviderFactory")
    void testAllDpsProDemo(DpsProDemoRequestDto dpsProDemoRequestDtoAll) throws ExecutionException, InterruptedException {

        when(dpsProDemoRepository.findByProductIdAndBrandId(dpsProDemoRequestDtoAll.getProductId(), dpsProDemoRequestDtoAll.getBrandId())).thenReturn(dpsProDemoList);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/dpsProDemo/{hour},{productId},{brandId}", dpsProDemoRequestDtoAll.getRequestDate()
                                    , dpsProDemoRequestDtoAll.getProductId(), dpsProDemoRequestDtoAll.getBrandId()
                            )
                            .contentType("application/json")
            ).andDo(print()).andExpect(status().isOk());
            DpsProDemoResponseDto responseDtoResult = dpsProDemoService.getCurrentDpsProDemoByProductIdAndBrandId(dpsProDemoRequestDtoAll).get();
            Assertions.assertAll(
                    () -> assertEquals(35455, responseDtoResult.getProductId()
                            , "ProductIOd " + responseDtoResult.getProductId()),
                    () -> assertEquals(1L, responseDtoResult.getBrandId()
                            , "branddD " + responseDtoResult.getBrandId()),
                    () -> assertEquals(true, responseDtoResult.getApplicationDates().size() > 0
                            , "mandatory aplication days" + responseDtoResult.getApplicationDates().size()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    static Iterator<DpsProDemoRequestDto> dpsProDemoRequestDtoProviderFactory() {
        List<DpsProDemoRequestDto> requestDtos = new ArrayList<>();
        requestDtos.add(DpsProDemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 10:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(DpsProDemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 16:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(DpsProDemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-14 21:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(DpsProDemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-15 10:00:00"))
                .productId(35455)
                .brandId(1L).build());
        requestDtos.add(DpsProDemoRequestDto.builder()
                .requestDate(Timestamp.valueOf("2020-06-16 21:00:00"))
                .productId(35455)
                .brandId(1L).build());
        return requestDtos.stream().iterator();
    }
    @Test
    void deleteInBatch() {
    }
}
