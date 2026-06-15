package ru.yandex.workshop.infoname.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.workshop.infoname.client.AgeClient;
import ru.yandex.workshop.infoname.service.InfoName;
import ru.yandex.workshop.infoname.model.InfoNameService;
import ru.yandex.workshop.ratelimiter.annotation.RateLimited;
import ru.yandex.workshop.ratelimiter.exceptions.RateLimitExceededException;

import java.util.Map;

@RestController
@RequestMapping("api/names/{name}/info")
public class InfoNameController {

  @Autowired
  private InfoNameService infoNameService;

  @Autowired
  private AgeClient ageClient;

  @GetMapping
  @RateLimited
  public InfoName infoName(@PathVariable String name) {
    return infoNameService.getInfo(name, ageClient.getAge(name));
  }
  
  @ExceptionHandler(RateLimitExceededException.class)
  @ResponseStatus(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED)
  public Object rateLimitExceeded(RateLimitExceededException e) {
    return Map.of(
      "Ошибка", "Превышено число допустимых запросов",
      "Совет", "Попробуйте позже"
    );
  }
}
