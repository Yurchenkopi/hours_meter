package ru.yurch.hours.controller;

import com.ibm.icu.text.Transliterator;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yurch.hours.model.Item;
import ru.yurch.hours.model.Report;
import ru.yurch.hours.model.User;
import ru.yurch.hours.service.ItemService;
import ru.yurch.hours.service.ReportService;
import ru.yurch.hours.service.UserService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

@Controller
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final ReportService reportService;

    private static final Logger LOG = LoggerFactory.getLogger(ItemController.class.getName());
    private static final Transliterator CYR_TO_LAT = Transliterator.getInstance("Russian-Latin/BGN");

    @GetMapping("/find")
    public String getByDate(
            Model model,
            @SessionAttribute(name = "user") User user,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        var today = LocalDate.now();
        if (endDate == null) {
            endDate = today.with(TemporalAdjusters.lastDayOfMonth());
        }
        if (startDate == null) {
            startDate = today.with(TemporalAdjusters.firstDayOfMonth());
        }
        model.addAttribute("currentStartDate", startDate);
        model.addAttribute("currentEndDate", endDate);
        var currentUser = userService.findByName(user.getUsername());
        if (currentUser.isPresent()) {
            if (currentUser.get().getAuthority().getAuthority().equals("ROLE_USER")) {
                System.out.println("Role User");
            } else if (currentUser.get().getAuthority().getAuthority().equals("ROLE_EMPLOYER")) {
                model.addAttribute("employees", userService.findBindedEmployees(currentUser.get().getId()));
                userService.findBindedEmployees(currentUser.get().getId()).forEach(System.out::println);
            }
            var rsl = itemService.findItemsByDate(startDate, endDate, currentUser.get());
            var report = itemService.updateExtraTime(rsl);
            rsl.forEach(System.out::println);
            model.addAttribute("itemsDtoMap", report.getContent());
            var minutes = report.getTimeInMinutes();
            model.addAttribute("sumOfTimeInMinutes", minutes);
            model.addAttribute("sumOfTimeInHours", (float) Math.round(minutes * 100 / 60) / 100);
            model.addAttribute("sumOfTimeInDays", (float) Math.round(minutes * 100 / (60 * 8)) / 100);
        }
        return "items/list";
    }

    @GetMapping("/update")
    public String updateItem(
            Model model,
            @SessionAttribute(name = "user") User user,
            @RequestParam(name = "id", required = false) int id) {
        var currentUser = userService.findByName(user.getUsername());
        if (currentUser.isPresent()) {
            var rsl = itemService.findItemById(id);
            if (rsl.isEmpty()) {
                model.addAttribute("message", "Не удалось найти выбранный интервал времени по указанному Id.");
                return "errors/404";
            }
            model.addAttribute("item", rsl.get());
        }
        return "items/edit";
    }

    @GetMapping("/add")
    public String addItem(Model model) {
        model.addAttribute("currentDate", LocalDate.now());
        model.addAttribute("startTime", LocalTime.of(9, 0));
        model.addAttribute("endTime", LocalTime.now());
        return "items/new";
    }

    @GetMapping("/remove")
    public String remove(
            Model model,
            @SessionAttribute(name = "user") User user,
            @RequestParam(name = "id", required = false) int id
    ) {
        var currentUser = userService.findByName(user.getUsername());
        if (currentUser.isPresent()) {
            var item = itemService.findItemById(id).get();
            item.setUser(userService.findByName(user.getUsername()).get());
            var isDeleted = itemService.delete(item);
            if (!isDeleted) {
                model.addAttribute("message", "Не удалось удалить временной интервал.");
                return "errors/404";
            }
        }
        return "redirect:/items/find";
    }

    @GetMapping("/generate-report")
    public void downloadReport(
            HttpServletResponse response,
            @SessionAttribute(name = "user") User user,
            Report report,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        var currentUser = userService.findByName(user.getUsername());
        if (currentUser.isPresent()) {
            var rsl = itemService.findItemsByDate(startDate, endDate, currentUser.get());
            report = itemService.updateExtraTime(rsl);
        }
        String fileName = String.format("attachment; filename=\"%s_%s.pdf\"",
                CYR_TO_LAT.transliterate(currentUser.get().getSurname()),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", fileName);
        reportService.createPDFReport(response.getOutputStream(), report, currentUser.get(), startDate, endDate);
    }


    @PostMapping("/add")
    public String addItem(
        @ModelAttribute Item item,
        @SessionAttribute(name = "user") User user,
        Model model
    ) {
        item.setUser(userService.findByName(user.getUsername()).get());
        LOG.info("Item {} was saved in DB", item);
        LOG.info("Item has \"extraHours\" label: {}", item.isExtraHoursOnly());
        var itemOptional = itemService.save(item);
        if (itemOptional.isEmpty()) {
            model.addAttribute("message", "Не удалось добавить временной интервал.");
            return "errors/404";
        }
        var message = String.format(
                "Временной интервал за %s добавлен в БД.",
                item.getDate());
        model.addAttribute("message", message);
        return "messages/message";
    }

    @PostMapping("/update")
    public String updateItem(
            @SessionAttribute(name = "user") User user,
            @RequestParam(name = "id", required = false) int id,
            @ModelAttribute Item item,
            Model model
    ) {
        item.setUser(userService.findByName(user.getUsername()).get());
        var isUpdated = itemService.update(item);
        if (!isUpdated) {
            model.addAttribute("message", "Не удалось произвести редактирование временного интервала.");
            return "errors/404";
        }
        var message = String.format(
                "Временной интервал с id=%s был успешно отредактирован.",
                item.getId());
        model.addAttribute("message", message);
        return "messages/message";
    }
}
