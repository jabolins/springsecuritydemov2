package com.example.springsecuritydemov2.rest;

import com.example.springsecuritydemov2.model.Developer;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/developers")

public class DeveloperRestControllerV1 {
    private List<Developer> ALL_DEVElOPERS = Stream.of(
            new Developer(1, "Juris", "Āboliņš")
            , new Developer(2, "Jānis", "Bērziņš")
            , new Developer(3, "Satna", "Krūmiņa")
    ).collect(Collectors.toList());


    @GetMapping
    public List<Developer> getAll() {
        return ALL_DEVElOPERS;
    }

    @GetMapping("/{id}")
    public Developer getById(@PathVariable Long id) {
        return ALL_DEVElOPERS.stream().filter(developer -> developer.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @PostMapping
    public Developer create(@RequestBody Developer developer) {
        this.ALL_DEVElOPERS.add(developer);
        return developer;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
this.ALL_DEVElOPERS.removeIf(developer -> developer.getId()==id);
    }

}
