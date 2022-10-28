package com.dpspro.devops.dtos;

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
public class DemoResponseDto {
    private Integer productId;
    private Long brandId;
    private Integer demoList;
    private List<LocalDate> applicationDates;
    private BigDecimal demoPrice;


}