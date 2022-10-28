package com.dpspro.devops.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class DemoRequestDto {
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    private Date requestDate;
    private Integer productId;
    private Long brandId;
}