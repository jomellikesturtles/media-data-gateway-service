package com.mdb.media_data_gateway_service.stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class StreamController {
    @GetMapping("/")
    public String getSomething() {
        return "HEY";
    }
}
