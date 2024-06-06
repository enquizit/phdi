package com.cdc.linkage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogsController {

  @GetMapping("")
  public String welcome(){
    return "welcome to logs";
  }
}
