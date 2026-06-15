package ru.yandex.workshop.ratelimiter.core;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import ru.yandex.workshop.ratelimiter.annotation.RateLimited;

@Aspect
public class RateLimiterAspect {

    private final RateLimiter rateLimiter;
    private final HttpServletRequest httpServletRequest;

    public RateLimiterAspect(RateLimiter rateLimiter, HttpServletRequest httpServletRequest) {
        this.rateLimiter = rateLimiter;
        this.httpServletRequest = httpServletRequest;
    }

    @Around(value = "@annotation(annotation)")
    public Object handleInRateLimiter(final ProceedingJoinPoint joinPoint, final RateLimited annotation) throws Throwable {
        rateLimiter.increment(httpServletRequest.getRemoteAddr() + ":" + httpServletRequest.getRequestURI());
        return joinPoint.proceed();
    }
}
