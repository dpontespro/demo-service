package com.dpspro.devops.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder

@Entity(name="demo")
public class  Demo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long brandId;
    private Date startDate;
    private Date endDate;

    @Column(name = "demo_list")
    private Integer demoList;
    private Integer productId;
    private Integer priority;
    @Column(name = "demo_price")
    private BigDecimal demoPrice;
    private String curr;

    @JsonIgnore
    @Transient
    private List<LocalDate> demoMandatoryDays;

    public Demo(Long brandId, Date startDate, Date endDate, Integer demoList, Integer productId, Integer priority, BigDecimal demoPrice, String curr) {
        this.brandId = brandId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.demoList = demoList;
        this.productId = productId;
        this.priority = priority;
        this.demoPrice = demoPrice;
        this.curr = curr;
    }

    public boolean validDemoRange(Date applicationTime) {
        return applicationDates(applicationTime);
    }

    private boolean applicationDates(Date applicationTime) {
        return this.startDate.compareTo(applicationTime) <= 0 && this.endDate.compareTo(applicationTime) >= 0;
    }

    public List<LocalDate> lookForApplicationDates(Date filterDate) {

        this.demoMandatoryDays = Collections.unmodifiableList(listDemoMandatoryDays(filterDate));
        return this.demoMandatoryDays;
    }

    private List<LocalDate> listDemoMandatoryDays(Date filterDate) {

        final List<LocalDate> localDateList = LocalDate.ofInstant(filterDate.toInstant(), ZoneId.of("UTC"))
                .datesUntil(LocalDate.ofInstant(this.endDate.toInstant(), ZoneId.of("UTC")))
                .collect(Collectors .toList());
        if (localDateList.isEmpty()) localDateList.add(LocalDate.ofInstant(filterDate.toInstant(), ZoneId.of("UTC")));

        return localDateList;
    }
}
