package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.FooService;
import de.yggdrasil128.factorial.model.Item;
import de.yggdrasil128.factorial.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class HelloController {
    private final ItemRepository itemRepository;
    private final FooService fooService;

    @Autowired
    public HelloController(ItemRepository itemRepository, FooService fooService) {
        this.itemRepository = itemRepository;
        this.fooService = fooService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/item")
    public Item getItem(int id) {
        Optional<Item> opt = itemRepository.findById(id);
        if (opt.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find item");
        }
        return opt.get();
    }

    @PostMapping("/item")
    public Item saveItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }

}
