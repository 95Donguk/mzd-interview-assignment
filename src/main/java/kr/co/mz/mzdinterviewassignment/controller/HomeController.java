package kr.co.mz.mzdinterviewassignment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

  @GetMapping
  public ResponseEntity<CollectionModel> redirect() {
    RestTemplate rt = new RestTemplate();
    return rt.exchange("http://localhost:8080/api/members", HttpMethod.GET, null,
        CollectionModel.class);
  }
}
