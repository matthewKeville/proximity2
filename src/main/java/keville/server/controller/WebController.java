package keville.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    private static final Logger LOG = LoggerFactory.getLogger(WebController.class);

    public WebController() {}

    @GetMapping(value = { "/", "/user/home", "/user/regions", "/user/events", "/user/compilers" })
    public String main() {
      return "main";
    }
}
