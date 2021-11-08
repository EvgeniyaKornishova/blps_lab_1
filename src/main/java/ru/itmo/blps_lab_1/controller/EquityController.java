package ru.itmo.blps_lab_1.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.itmo.blps_lab_1.data.Equity;
import ru.itmo.blps_lab_1.data.dto.EquityDto;
import ru.itmo.blps_lab_1.repository.EquityRepository;

import java.util.List;
import java.util.Optional;

@Transactional
@RestController
@RequestMapping("/equities")
public class EquityController {

    @Autowired
    EquityRepository equityRepository;

    @GetMapping("/")
    @ApiOperation(value = "Get list of equities", response = EquityDto.class, responseContainer = "List")
    public ResponseEntity<?> listEquities(){
        List<Equity> equities = equityRepository.findAll();
        return new ResponseEntity<List<EquityDto>>(EquityDto.fromEquitiesList(equities), HttpStatus.OK);
    }

    @GetMapping("/{equity_id}")
    @ApiOperation(value = "Get equity by id", response = EquityDto.class)
    public ResponseEntity<?> getEquity(@PathVariable("equity_id") Long id){
        Optional<Equity> equity = equityRepository.findById(id);
        if (!equity.isPresent())
            return new ResponseEntity<String>("Equity with specified id not found", HttpStatus.NOT_FOUND);

       return new ResponseEntity<EquityDto>(EquityDto.fromEquity(equity.get()), HttpStatus.OK);
    }

}
