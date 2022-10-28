package com.dpspro.devops.services;

import com.dpspro.devops.dtos.DemoRequestDto;
import com.dpspro.devops.dtos.DemoResponseDto;
import com.dpspro.devops.entities.Demo;
import com.dpspro.devops.exceptions.DemoNotFoundException;
import com.dpspro.devops.repositories.DemoRepository;
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
public class DemoAsyncService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DemoAsyncService.class);
    @Autowired
    DemoRepository demoRepository;

    public DemoAsyncService(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    @Async("asyncTaskExecutor")
    public Future<DemoResponseDto> getCurrentDemoByProductIdAndBrandId(DemoRequestDto demoFilterParams) throws DemoNotFoundException {
        try {
            LOGGER.info(" asyncDemoResponmse Start processing " + LocalDateTime.now());
            TimeUnit.MILLISECONDS.sleep(1000);
            return new AsyncResult<>(
                    entityToDto(
                            getDemo(demoFilterParams
                                    , demoRepository
                                            .findByProductIdAndBrandId(demoFilterParams.getProductId(), demoFilterParams.getBrandId())
                                            .stream()
                                            .filter(demo -> demo.validDemoRange(demoFilterParams.getRequestDate()))
                                            .collect(Collectors.toList()))
                            , demoFilterParams.getRequestDate()));


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    protected Demo getDemo(DemoRequestDto demoFilterParams, List<Demo> demo) {
        boolean seen = false;
        Demo best = null;
        Comparator<Demo> comparator = Comparator.comparing(Demo::getPriority);
        for (Demo demo1 : demo) {
            if (!seen || comparator.compare(demo1, best) > 0) {
                seen = true;
                best = demo1;
            }
        }
        return (seen ? Optional.of(best) : Optional.<Demo>empty())
                .orElseThrow(() -> new DemoNotFoundException(HttpStatus.NOT_FOUND,
                        "for productId :" + demoFilterParams.getProductId() + " and date " + demoFilterParams.getRequestDate()));
    }


    protected DemoResponseDto entityToDto(Demo demo, Date filterDate) {

        return new DemoResponseDto(demo.getProductId(), demo.getBrandId(), demo.getDemoList()
                , demo.lookForApplicationDates(filterDate), demo.getDemoPrice());

    }


}
