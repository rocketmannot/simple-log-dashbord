package com.papchenko.logagent.web;

import com.papchenko.logagent.service.WatchRegistrationService;
import com.papchenko.logagent.service.impl.RegistrationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@Slf4j
@RestController
@RequestMapping("/watch")
public class WatchController {

    @Autowired
    private WatchRegistrationService<Path> watchRegistrationService;

    //todo make put mapping
    @PostMapping
    public ResponseEntity<String> registerWatchedFile(@RequestBody String path) {
        try {
            Path file = Paths.get(path);
            return ResponseEntity.ok(watchRegistrationService.registerWatchedFile(file));
        } catch (InvalidPathException e) {
            log.warn("path does not exist {}", path);
            return ResponseEntity.badRequest().body("Path does not exists");
        } catch (RegistrationException e) {
            log.warn("failed to register path " + path, e);
            return ResponseEntity.badRequest().body("failed to register file for watching");
        }
    }

    @PostMapping("/{key}")
    public void notifyLogChangeConsumed(@PathVariable("key") String key) {
        watchRegistrationService.notifyMessageConsumed(key);
        log.debug("change consumed {}", key);
    }
}
