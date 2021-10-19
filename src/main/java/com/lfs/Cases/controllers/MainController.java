package com.lfs.Cases.controllers;

import com.lfs.Cases.models.Item;
import com.lfs.Cases.repositories.ItemRepository;
import com.lfs.Cases.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Controller
public class MainController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/inventory")
    public String inv(Model model) {
        Iterable<Item> items = itemRepository.findAll();
        int counter = 0;
        for (Item i : items) {
            counter++;
        }
        if (counter == 0) {
            model.addAttribute("no_items", "you have no items available yet");
        }
        model.addAttribute("items", items);
        return "my_cases";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        return "add";
    }

    @PostMapping("/add")
    public String add(@RequestParam(defaultValue = "") String naming, @RequestParam(defaultValue = "") Long pricing, Model model) {
        if (naming.length() == 0 || pricing == null) {
            model.addAttribute("error_type", "Error: bad naming");
            return "error_page";
        }
        Item item = new Item(naming, pricing);
        itemRepository.save(item);
        return "redirect:/inventory";
    }

    @PostMapping("/del")
    public String delete(@RequestParam Long elementID) {
        itemRepository.deleteById(elementID);
        return "redirect:/inventory";
    }

    @PostMapping("/my_new_item")
    public String getRandomItem(Model model) {
        Iterable<Item> items = itemRepository.findAll();
        Stream<Item> stream = StreamSupport.stream(items.spliterator(), false);
        long count = stream.count();
        if (count == 0) {
            model.addAttribute("error_type", "Error: no items are available");
            return "error_page";
        }
        Stream<Item> stream2 = StreamSupport.stream(items.spliterator(), false);
        Item item = stream2.skip(ThreadLocalRandom.current().nextLong(count)).findAny().get();
        model.addAttribute("item", item);
        return "my_item";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }


    @GetMapping("/sign_up")
    public String getLogin(Model model) {
        return "signup";
    }

}
