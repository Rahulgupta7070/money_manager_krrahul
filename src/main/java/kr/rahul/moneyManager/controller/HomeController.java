package kr.rahul.moneyManager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/status","/rahul"})
public class HomeController {
	
	@GetMapping
	public String healthCheck() {
		return "Rahul is renuning";
	}

}
