package pers.jenkins.demo.apis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping("/v1")
	public String getMessage() {
		return "Hello World";
	}

}
