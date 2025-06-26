package tw.amer.cia.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/dns")
public class DnsController {

    @Value("${CIA_DEFAULT_DNS_SERVER:}")
    private String defaultDnsServer;

    @PostConstruct
    public void initDnsServer(){
        if(StringUtils.isNotBlank(defaultDnsServer)){
            log.info("Initializing Dns Server by default: {}" , defaultDnsServer);
            this.setDnsServer(defaultDnsServer);
        }
    }

    @PostMapping("/config")
    public ResponseEntity<String> setDnsServer(@RequestParam String dnsServer) {
        try {
            // 驗證DNS服務器格式
            if (!isValidIpAddress(dnsServer)) {
                return ResponseEntity.badRequest().body("Invalid DNS server IP format");
            }

            // 設置DNS服務器
            java.security.Security.setProperty("sun.net.spi.nameservice.nameservers", dnsServer);

            // 如果使用的是IPv4，可能需要以下設置
            System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");

            log.info("DNS server changed to: " + dnsServer);
            return ResponseEntity.ok("DNS server changed to: " + dnsServer);
        } catch (Exception e) {
            log.error("Failed to change DNS server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to change DNS server: " + e.getMessage());
        }
    }

    @GetMapping("/config")
    public ResponseEntity<String> getCurrentDnsServer() {
        String currentDns = java.security.Security.getProperty("sun.net.spi.nameservice.nameservers");
        return ResponseEntity.ok("Current DNS server: " + (currentDns != null ? currentDns : "system default"));
    }

    @GetMapping("/resolve")
    public ResponseEntity<Map<String, Object>> resolveDomain(@RequestParam String domain) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 獲取當前DNS服務器
            String currentDns = java.security.Security.getProperty("sun.net.spi.nameservice.nameservers");
            if (currentDns == null) {
                currentDns = "system default";
            }

            // 執行DNS解析
            InetAddress[] addresses = InetAddress.getAllByName(domain);
            List<String> ipAddresses = new ArrayList<>();

            for (InetAddress address : addresses) {
                ipAddresses.add(address.getHostAddress());
            }

            // 構建響應
            response.put("success", true);
            response.put("domain", domain);
            response.put("ipAddresses", ipAddresses);
            response.put("dnsServer", currentDns);

            log.info("Successfully resolved domain " + domain + " using DNS " + currentDns);
            return ResponseEntity.ok(response);

        } catch (UnknownHostException e) {
            log.warn("Failed to resolve domain " + domain + ": " + e.getMessage());

            response.put("success", false);
            response.put("domain", domain);
            response.put("error", "Failed to resolve domain: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            log.warn("Error resolving domain " + domain + ": " + e.getMessage());

            response.put("success", false);
            response.put("domain", domain);
            response.put("error", "Error resolving domain: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean isValidIpAddress(String ip) {
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
