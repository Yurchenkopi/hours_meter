package ru.yurch.hours.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yurch.hours.model.User;
import ru.yurch.hours.service.ItemService;
import ru.yurch.hours.service.UserService;

import java.time.LocalDate;

@Controller
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    private static final Logger LOG = LoggerFactory.getLogger(ItemController.class.getName());

    @GetMapping("/find")
    public String getByDate(
            Model model,
            @RequestAttribute(name = "user") User user,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            ) {
        var currentUser = userService.findByName(user.getUsername());
        if (currentUser.isPresent() && startDate != null && endDate != null) {
            var rsl = itemService.findItemsByDate(startDate, endDate, currentUser.get());
            rsl.forEach(System.out::println);
            model.addAttribute("items", rsl);
        }
        return "items/list";
    }
}
