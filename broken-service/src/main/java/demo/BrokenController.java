package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BrokenController {
    @Autowired
    private BrokenService brokenService;

    @RequestMapping("/test")
    public ResponseEntity<String> runTest() throws Exception {
        return Optional.ofNullable(brokenService.stringify())
                .map(username -> new ResponseEntity<>(username, HttpStatus.OK))
                .orElseThrow(() -> new Exception("Boom!"));
    }
}