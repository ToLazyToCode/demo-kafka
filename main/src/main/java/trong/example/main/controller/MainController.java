package trong.example.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import trong.example.main.service.FacadeService;


@RestController
@RequestMapping("/api/v1/main")
@RequiredArgsConstructor
public class MainController {

    private final FacadeService facadeService;

    @GetMapping("/test")
    public String test(@RequestParam String email, @RequestParam String message) {
        return facadeService.testKafka(email, message);
    }
}
