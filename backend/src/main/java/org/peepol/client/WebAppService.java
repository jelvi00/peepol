package org.peepol.client;

import org.peepol.client.response.WebAppHealthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "webapp-service", url = "${app.web.url}")
public interface WebAppService {

    @GetMapping(value = "/api/health", produces = "application/json")
    WebAppHealthResponse health();

}
