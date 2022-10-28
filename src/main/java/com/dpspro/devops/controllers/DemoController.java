package com.dpspro.devops.controllers;

import com.dpspro.devops.dtos.DemoRequestDto;
import com.dpspro.devops.dtos.DemoResponseDto;
import com.dpspro.devops.exceptions.DemoNotFoundException;
import com.dpspro.devops.services.DemoAsyncService;
import com.dpspro.devops.utility.DemoServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private final DemoAsyncService demoAsyncService;

    @Autowired
    public DemoController(DemoAsyncService demoAsyncService) {
        this.demoAsyncService = demoAsyncService;
    }

    @GetMapping(value = "/{hour},{productId},{brandId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DemoResponseDto> getDemo(@PathVariable (value = "hour")
                                                     @DateTimeFormat(pattern = DemoServiceUtils.FORMATO_FECHA) final Date hour,
                                                     @PathVariable (value="productId") Integer productId,
                                                     @PathVariable (value = "brandId") Long brandId) throws DemoNotFoundException, InterruptedException, ExecutionException {
        final DemoRequestDto demoFilterParams = new DemoRequestDto(hour, productId, brandId);
        DemoResponseDto respFuture = demoAsyncService.getCurrentDemoByProductIdAndBrandId(demoFilterParams).get();
        if (respFuture != null) {
            return new ResponseEntity<>(respFuture, HttpStatus.OK);
        } else {
            throw new DemoNotFoundException();
        }
    }



}
