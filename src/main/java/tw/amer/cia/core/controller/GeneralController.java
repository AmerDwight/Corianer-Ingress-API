package tw.amer.cia.core.controller;

import tw.amer.cia.core.service.core.GeneralService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GeneralController {
    @Autowired
    GeneralService generalService;

    @RequestMapping(value = "/healthcheck")
    public ResponseEntity<Void> healthCheck() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/identify")
    public Object identify() {
        ApIdentification apIdentification = new ApIdentification(generalService.getDeployType());
        return new ResponseEntity<>(apIdentification, HttpStatus.OK);
    }

    @RequestMapping(value = "/version")
    public Object version() {
        return new ResponseEntity<>(generalService.getVersion(), HttpStatus.OK);
    }

    @RequestMapping(value = "/config")
    public Object config() {
        return new ResponseEntity<>(generalService.getConfig(), HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class ApIdentification {
        @JsonProperty("DEPLOY_TYPE")
        String deployType;
    }
}
