package com.dpontespro.devops.entities;

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
public class  DpsProDemo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long brandId;
    private Date startDate;
    private Date endDate;

    @Column(name = "demo_list")
    private Integer dpsProDemoList;
    private Integer productId;
    private Integer priority;
    @Column(name = "demo_price")
    private BigDecimal dpsProDemo;
    private String curr;

    @JsonIgnore
    @Transient
    private List<LocalDate> dpsProDemoMandatoryDays;

    public DpsProDemo(Long brandId, Date startDate, Date endDate, Integer dpsProDemoList, Integer productId, Integer priority, BigDecimal dpsProDemo, String curr) {
        this.brandId = brandId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dpsProDemoList = dpsProDemoList;
        this.productId = productId;
        this.priority = priority;
        this.dpsProDemo = dpsProDemo;
        this.curr = curr;
    }

    public boolean validDpsProDemoRange(Date applicationTime) {
        return applicationDates(applicationTime);
    }

    private boolean applicationDates(Date applicationTime) {
        return this.startDate.compareTo(applicationTime) <= 0 && this.endDate.compareTo(applicationTime) >= 0;
    }

    public List<LocalDate> lookForApplicationDates(Date filterDate) {

        for (LocalDate localDate : this.dpsProDemoMandatoryDays = Collections.unmodifiableList(listDpsProDemoMandatiryDays(filterDate))) {
        }
        return this.dpsProDemoMandatoryDays;
    }

    private List<LocalDate> listDpsProDemoMandatiryDays(Date filterDate) {

        final List<LocalDate> localDateList = LocalDate.ofInstant(filterDate.toInstant(), ZoneId.of("UTC"))
                .datesUntil(LocalDate.ofInstant(this.endDate.toInstant(), ZoneId.of("UTC")))
                .collect(Collectors .toList());
        if (localDateList.isEmpty()) localDateList.add(LocalDate.ofInstant(filterDate.toInstant(), ZoneId.of("UTC")));

        return localDateList;
    }
}
