package com.example.gdg.domain.provider.cloudflare.client;

import com.example.gdg.domain.provider.cloudflare.dto.req.CloudflareDnsReq;
import com.example.gdg.domain.provider.cloudflare.dto.res.CloudflareDnsRes;
import com.example.gdg.domain.dns.entity.DnsRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
@RequiredArgsConstructor
public class CloudflareApiService {

    @Value("${cloudflare.api-token}")
    private String apiToken;

    @Value("${cloudflare.zone-id}")
    private String zoneId;

    @Value("${cloudflare.base-url}")
    private String baseUrl;

    private final RestClient restClient = RestClient.create();

    public String createRecord(String recordName, DnsRecord record) {
        CloudflareDnsReq requestBody = CloudflareDnsReq.builder()
                .type(record.getType().name())
                .name(recordName)
                .content(record.getValue())
                .ttl(record.getTtl())
                .proxied(record.getProxied())
                .comment("Created by GDG Project")
                .build();

        try {
            CloudflareDnsRes response = restClient.post()
                    .uri(baseUrl + "/zones/" + zoneId + "/dns_records")
                    .header("Authorization", "Bearer " + apiToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(CloudflareDnsRes.class);

            if (response != null && response.isSuccess()) {
                return response.getResult().getId();
            }
            throw new RuntimeException("Cloudflare API Create Failed");
        } catch (RestClientResponseException e) {
            throw new IllegalArgumentException("Cloudflare가 DNS 레코드 생성을 거부했습니다.");
        }
    }

    public void updateRecord(String recordName, String cloudflareId, DnsRecord record) {
        CloudflareDnsReq requestBody = CloudflareDnsReq.builder()
                .type(record.getType().name())
                .name(recordName)
                .content(record.getValue())
                .ttl(record.getTtl())
                .proxied(record.getProxied())
                .build();

        try {
            restClient.put()
                    .uri(baseUrl + "/zones/" + zoneId + "/dns_records/" + cloudflareId)
                    .header("Authorization", "Bearer " + apiToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw new IllegalArgumentException("Cloudflare가 DNS 레코드 수정을 거부했습니다.");
        }
    }

    public void deleteRecord(String cloudflareId) {
        restClient.delete()
                .uri(baseUrl + "/zones/" + zoneId + "/dns_records/" + cloudflareId)
                .header("Authorization", "Bearer " + apiToken)
                .retrieve()
                .toBodilessEntity();
    }
}


