package com.lfs.Cases.controllers;

import com.lfs.Cases.models.Item;
import com.lfs.Cases.models.User;
import com.lfs.Cases.repositories.ItemRepository;
import com.lfs.Cases.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Controller
@SessionAttributes("userid")
public class MainController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/")
    public ModelAndView index(@ModelAttribute("userid") long userid, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        if(userid == -1){
            modelAndView.setViewName("login");
            return modelAndView;
        }

        modelAndView.addObject("userid", userid);
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @ModelAttribute("userid")
    public long createUserid(){
        return -1;
    }

    @GetMapping("/inventory")
    public String inv(@ModelAttribute("userid") long userid, Model model) {
        Iterable<Item> items = itemRepository.findAll();
        ArrayList<Item> new_items = new ArrayList<>();
        int counter = 0;
        for (Item i : items) {
            if (i.getUser().getId().equals(userid)){
                counter++;
                new_items.add(i);
            }
        }
        if (counter == 0) {
            model.addAttribute("no_items", "you have no items available yet");
        }
        model.addAttribute("items", new_items);
        return "my_cases";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        return "add";
    }

    @GetMapping("/index")
    public String idexredir(Model model) {
        return "redirect:/";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("userid") long userid, @RequestParam(defaultValue = "") String naming, @RequestParam(defaultValue = "") Long pricing, Model model) {
        if (naming.length() == 0 || pricing == null) {
            model.addAttribute("error_type", "Error: bad naming");
            return "error_page";
        }
        System.out.println(userid);
        Item item = new Item(naming, pricing, userRepository.findById(userid).get());
        itemRepository.save(item);
        return "redirect:/inventory";
    }

    @PostMapping("/del")
    public String delete(@RequestParam Long elementID) {
        itemRepository.deleteById(elementID);
        return "redirect:/inventory";
    }

    @PostMapping("/my_new_item")
    public String getRandomItem(@ModelAttribute("userid") long userid, Model model) {
        Iterable<Item> items = itemRepository.findAll();
        Stream<Item> stream = StreamSupport.stream(items.spliterator(), false);
        long count = stream.filter(o-> o.getUser().getId() != userid).count();
        if (count == 0) {
            model.addAttribute("error_type", "Error: no items are available");
            return "error_page";
        }
        Stream<Item> stream2 = StreamSupport.stream(items.spliterator(), false);
        Item item = stream2.filter(o-> o.getUser().getId() != userid).skip(ThreadLocalRandom.current().nextLong(count)).findAny().get();
        itemRepository.save(new Item(item.getName(),item.getPrice(),userRepository.findById(userid).get()));
        model.addAttribute("item", item);
        return "my_item";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @PostMapping(value = "/", params = "signup")
    public ModelAndView register(@ModelAttribute("userid") long userid,@RequestParam() String signup, @RequestParam(defaultValue = "") String login, @RequestParam(defaultValue = "") String password, Model model) {

        ModelAndView modelAndView = new ModelAndView();
        if (login.length() == 0 || password.length() == 0) {
            model.addAttribute("error_type", "Error: bad login/password");
            modelAndView.setViewName("error_page");
            return modelAndView;
        }


        Iterable<User> users = userRepository.findAll();
        Stream<User> stream = StreamSupport.stream(users.spliterator(), false);
        try{
            stream.filter(o -> o.getName().equals(login)).findAny().get();
            model.addAttribute("error_type", "Error: user already exists");
            modelAndView.setViewName("error_page");
            return modelAndView;
        }
        catch (NoSuchElementException e){}

        User user = new User(login,password);
        userRepository.save(user);
        modelAndView.addObject("userid", user.getId());
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @PostMapping(value = "/", params = "logen")
    public ModelAndView login(@ModelAttribute("userid") long userid, @RequestParam(defaultValue = "") String login, @RequestParam(defaultValue = "") String password, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        if (login.length() == 0 || password.length() == 0) {
            model.addAttribute("error_type", "Error: bad login/password");
            modelAndView.setViewName("error_page");
            return modelAndView;
        }
        long idd = -1;
        Iterable<User> users = userRepository.findAll();
        Stream<User> stream = StreamSupport.stream(users.spliterator(), false);
        User us;
        try{
            us = stream.filter(o -> o.getPassword().equals(password) && o.getName().equals(login)).findAny().get();
        }
        catch (NoSuchElementException e){
            us = null;
        }

        if( us == null ){
            model.addAttribute("error_type", "Error: no user");
            modelAndView.setViewName("error_page");
            return modelAndView;
        }
        idd = us.getId();
        modelAndView.addObject("userid", idd);
        modelAndView.setViewName("index");
        return modelAndView;
    }

}
