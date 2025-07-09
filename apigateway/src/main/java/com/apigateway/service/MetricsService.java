package com.apigateway.service;


import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

//@Service
public class MetricsService {

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public MetricsService(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = new RestTemplate();
    }

    public void fetchServiceMetrics(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

        for (ServiceInstance instance : instances) {
            String url = instance.getUri().toString() + "/actuator/metrics/system.cpu.usage";

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                System.out.println("Metrics from " + instance.getHost() + ": " + response.getBody());

            } catch (Exception e) {
                System.out.println("Failed to fetch metrics from " + instance.getHost() + ": " + e.getMessage());
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    public void monitorServices() {
        fetchServiceMetrics("productservice");
        fetchServiceMetrics("userservice");
        fetchServiceMetrics("orderservice");
    }

    public ServiceInstance chooseLighterInstance(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

        ServiceInstance selectedInstance = null;
        double minCpuUsage = Double.MAX_VALUE;

        for (ServiceInstance instance : instances) {
            double cpuUsage = getCpuUsage(instance);
            System.out.println("Instance " + instance.getHost() + " CPU: " + cpuUsage);

            if (cpuUsage < minCpuUsage) {
                minCpuUsage = cpuUsage;
                selectedInstance = instance;
            }
        }
        return selectedInstance;
    }

    public double getCpuUsage(ServiceInstance instance) {
        String url = instance.getUri().toString() + "/actuator/metrics/system.cpu.usage";
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("measurements")) {
                List<Map<String, Object>> measurements = (List<Map<String, Object>>) body.get("measurements");
                if (!measurements.isEmpty()) {
                    return (Double) measurements.get(0).get("value");
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch CPU usage from " + instance.getHost());
        }
        return Double.MAX_VALUE;
    }

}

