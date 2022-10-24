package com.dpontespro.devops.services;

import com.dpontespro.devops.dtos.DpsProDemoRequestDto;
import com.dpontespro.devops.dtos.DpsProDemoResponseDto;
import com.dpontespro.devops.entities.DpsProDemo;
import com.dpontespro.devops.exceptions.DpsProDemoNotFoundException;
import com.dpontespro.devops.repositories.DpsProDemoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Transactional(readOnly = true)
@Service
public class DpsProDemoAsyncService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DpsProDemoAsyncService.class);
    @Autowired
    DpsProDemoRepository dpsProDemoRepository;

    public DpsProDemoAsyncService(DpsProDemoRepository dpsProDemoRepository) {
        this.dpsProDemoRepository = dpsProDemoRepository;
    }

    @Async("asyncTaskExecutor")
    public Future<DpsProDemoResponseDto> getCurrentDpsProDemoByProductIdAndBrandId(DpsProDemoRequestDto dpsProDemoFilterParams) throws DpsProDemoNotFoundException {
        try {
            LOGGER.info(" asyncDpsProDemoResponmse Start processing " + LocalDateTime.now());
            TimeUnit.MILLISECONDS.sleep(1000);
            return new AsyncResult<>(
                    entityToDto(
                            getDpsProDemo(dpsProDemoFilterParams
                                    , dpsProDemoRepository
                                            .findByProductIdAndBrandId(dpsProDemoFilterParams.getProductId(), dpsProDemoFilterParams.getBrandId())
                                            .stream()
                                            .filter(dpsProDemo -> dpsProDemo.validDpsProDemoRange(dpsProDemoFilterParams.getRequestDate()))
                                            .collect(Collectors.toList()))
                            , dpsProDemoFilterParams.getRequestDate()));


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    protected DpsProDemo getDpsProDemo(DpsProDemoRequestDto dpsProDemoFilterParams, List<DpsProDemo> dpsProDemo) {
        boolean seen = false;
        DpsProDemo best = null;
        Comparator<DpsProDemo> comparator = Comparator.comparing(DpsProDemo::getPriority);
        for (DpsProDemo dpsProDemo1 : dpsProDemo) {
            if (!seen || comparator.compare(dpsProDemo1, best) > 0) {
                seen = true;
                best = dpsProDemo1;
            }
        }
        return (seen ? Optional.of(best) : Optional.<DpsProDemo>empty())
                .orElseThrow(() -> new DpsProDemoNotFoundException(HttpStatus.NOT_FOUND,
                        "for productId :" + dpsProDemoFilterParams.getProductId() + " and date " + dpsProDemoFilterParams.getRequestDate()));
    }


    protected DpsProDemoResponseDto entityToDto(DpsProDemo dpsProDemo, Date filterDate) {

        return new DpsProDemoResponseDto(dpsProDemo.getProductId(), dpsProDemo.getBrandId(), dpsProDemo.getDpsProDemoList()
                , dpsProDemo.lookForApplicationDates(filterDate), dpsProDemo.getDpsProDemo());

    }


}
