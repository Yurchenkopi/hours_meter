package ru.yurch.hours.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import ru.yurch.hours.model.User;
import ru.yurch.hours.service.ItemService;

import java.time.LocalDate;

@Controller
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    private static final Logger LOG = LoggerFactory.getLogger(ItemController.class.getName());

    @GetMapping("/find")
    public String getByDate(
            Model model,
            @SessionAttribute(name = "user") User user,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            ) {
        LOG.info("Start date is: " + startDate.toString());
        LOG.info("End date is: " + endDate.toString());
        var rsl = itemService.findItemsByDate(startDate, endDate, user);
        rsl.forEach(System.out::println);
        model.addAttribute("items", rsl);
        return "items/list";
    }
}
