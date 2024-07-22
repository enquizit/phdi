package com.cdc.linkage.controller;



import com.cdc.linkage.model.InsertionResponse;
import com.cdc.linkage.model.RecordLinkageRequest;
import com.cdc.linkage.service.LinkageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;



@RestController
@RequestMapping("/api/linkage/")
@RequiredArgsConstructor
public class LinkageController {

  private final LinkageService linkageService;

  @PostMapping("record-linkage")
  public Mono<ResponseEntity<InsertionResponse>> recordLinkage(@RequestBody RecordLinkageRequest request)
      throws Exception {
      return linkageService.recordLinkage(request)  // Returns a Mono<InsertionResponse>
          .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
          .onErrorResume(Exception.class, ex ->
              Mono.just(ResponseEntity.badRequest().body(new InsertionResponse(null, ex.getMessage()))));
    }
  }

