package com.merfonteen.productcatalog.util;

import com.merfonteen.productcatalog.exception.TooManyRequestsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class RequestRateLimiter {

    private final StringRedisTemplate stringRedisTemplate;

    private final Long MAX_REQUESTS = 10L;
    private final Duration DURATION = Duration.ofMinutes(1);

    public void limitRequestsByUserId(Long userId) {
        String cacheKey = "limit::product::actions::user::" + userId;
        Long counter = stringRedisTemplate.opsForValue().increment(cacheKey);

        if(counter > 0) {
            stringRedisTemplate.expire(cacheKey, DURATION);
        }

        if(counter > MAX_REQUESTS) {
            throw new TooManyRequestsException("You have exceeded the allowed number of actions, max: " + MAX_REQUESTS);
        }
    }
}
