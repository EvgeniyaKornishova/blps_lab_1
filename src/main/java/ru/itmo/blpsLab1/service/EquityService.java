package ru.itmo.blpsLab1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itmo.blpsLab1.data.Equity;
import ru.itmo.blpsLab1.repository.EquityRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EquityService {
    @Autowired
    EquityRepository equityRepository;

    public Optional<Equity> findById(Long id) {
        return equityRepository.findById(id);
    }

    public List<Equity> findAll() {
        return equityRepository.findAll();
    }
}
