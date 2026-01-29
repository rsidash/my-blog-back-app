package kz.rsidash.controller;

import kz.rsidash.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    @GetMapping(Constants.HEALTH_CHECK)
    public String healthCheck() {
        return "OK";
    }

}
