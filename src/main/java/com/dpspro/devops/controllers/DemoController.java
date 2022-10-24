package com.dpontespro.devops.controllers;

import com.dpontespro.devops.dtos.DpsProDemoRequestDto;
import com.dpontespro.devops.dtos.DpsProDemoResponseDto;
import com.dpontespro.devops.exceptions.DpsProDemoNotFoundException;
import com.dpontespro.devops.services.DpsProDemoAsyncService;
import com.dpontespro.devops.utility.DpsProDemoServiceUtils;
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
@RequestMapping("/dpsProDemo")
public class DpsProDemoController {

    @Autowired
    private final DpsProDemoAsyncService dpsProDemoAsyncService;

    @Autowired
    public DpsProDemoController(DpsProDemoAsyncService dpsProDemoAsyncService) {
        this.dpsProDemoAsyncService = dpsProDemoAsyncService;
    }

    @GetMapping(value = "/{hour},{productId},{brandId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DpsProDemoResponseDto> getDpsProDemo(@PathVariable (value = "hour")
                                                     @DateTimeFormat(pattern = DpsProDemoServiceUtils.FORMATO_FECHA) final Date hour,
                                                     @PathVariable (value="productId") Integer productId,
                                                     @PathVariable (value = "brandId") Long brandId) throws DpsProDemoNotFoundException, InterruptedException, ExecutionException {
        final DpsProDemoRequestDto dpsProDemoFilterParams = new DpsProDemoRequestDto(hour, productId, brandId);
        DpsProDemoResponseDto respFuture = dpsProDemoAsyncService.getCurrentDpsProDemoByProductIdAndBrandId(dpsProDemoFilterParams).get();
        if (respFuture != null) {
            return new ResponseEntity<>(respFuture, HttpStatus.OK);
        } else {
            throw new DpsProDemoNotFoundException();
        }
    }



}
