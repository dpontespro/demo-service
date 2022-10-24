package com.dpontespro.devops.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class DpsProDemoResponseDto {
    private Integer productId;
    private Long brandId;
    private Integer dpsProDemoList;
    private List<LocalDate> applicationDates;
    private BigDecimal dpsProDemo;


}