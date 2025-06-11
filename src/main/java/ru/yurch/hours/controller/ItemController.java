package ru.yurch.hours.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            ) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userService.findByName(userDetails.getUsername());
        LOG.info("Current user is: " + user);
        LOG.info("Start date is: " + startDate.toString());
        LOG.info("End date is: " + endDate.toString());
        if (user.isPresent()) {
            var rsl = itemService.findItemsByDate(startDate, endDate, user.get());
            rsl.forEach(System.out::println);
            model.addAttribute("items", rsl);
        }
        return "items/list";
    }
}
