package com.example.rqchallenge.commonutils;

import com.example.rqchallenge.aspects.Timed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RestExecutorService {
    private final RestTemplate restTemplate;


    /**
     * @param url
     * @param httpEntity
     * @param httpMethod
     * @param responseClass
     * @return ResponseEntity of REST API call
     */
    @Retryable(
            value = {HttpClientErrorException.TooManyRequests.class},
            maxAttempts = 10,
            backoff = @Backoff(delay = 2000L, multiplier = 1.5D, maxDelay = 20000L))
    @Timed
    public <T> ResponseEntity<T> execute(String url, HttpEntity<?> httpEntity, HttpMethod httpMethod, Class<T> responseClass) {
        try {
            log.trace("Calling {} with {} method", url, httpMethod.name());
            return restTemplate.exchange(url, httpMethod, httpEntity, responseClass);
        } catch (HttpClientErrorException.TooManyRequests ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while for url {} with {} method :: ", url, httpMethod.name(), ex);
            throw ex;
        }
    }

}
