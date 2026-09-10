package com.architecture.solution.service.impl;

import com.architecture.solution.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public void hello() {
        log.info("Hello from HelloServiceImpl");
    }
}
