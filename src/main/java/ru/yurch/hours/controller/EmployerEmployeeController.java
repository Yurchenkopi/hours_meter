package ru.yurch.hours.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yurch.hours.dto.EmployerEmployeeDto;
import ru.yurch.hours.model.EmployerEmployee;
import ru.yurch.hours.model.User;
import ru.yurch.hours.service.EmployerEmployeeService;
import ru.yurch.hours.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
public class EmployerEmployeeController {
    private final EmployerEmployeeService employerEmployeeService;
    private final UserService userService;

    @GetMapping({"/bindlist"})
    public String getBindedEmployees(
            Model model,
            @SessionAttribute(name = "user") User user
    ) {
        List<EmployerEmployee> rsl = new ArrayList<>();
        var currentUser = userService.findByName(user.getUsername());
        if (currentUser.isPresent()) {
            if (userService.isAdmin(currentUser.get())) {
                rsl = employerEmployeeService.findAll();
            }
            model.addAttribute("employersEmployees", rsl);
        }
        return "ee/list";
    }

    @PostMapping("/unbind")
    public String remove(
            @ModelAttribute EmployerEmployeeDto eeDto,
            @SessionAttribute(name = "user") User user,
            RedirectAttributes redirectAttributes
    ) {
        var isDeleted = false;
        var currentUser = userService.findByName(user.getUsername());
        if (currentUser.isPresent()) {
            if (userService.isAdmin(currentUser.get())) {
                var ee = employerEmployeeService.findById(eeDto.getEeId());
                if (ee.isPresent()) {
                    isDeleted = employerEmployeeService.delete(ee.get());
                }
            }
            if (!isDeleted) {
                redirectAttributes.addFlashAttribute("errorMessage", "Переданы неверные параметры запроса. Повторите попытку позже.");
                return "redirect:/bindlist";
            }
            String message = String.format("Пользователь %s отвязан от работодателя %s.", eeDto.getEmployeeSurname(), eeDto.getEmployerSurname());
            redirectAttributes.addFlashAttribute("message", message);
        }
        return "redirect:/bindlist";
    }

    @GetMapping("/unbind")
    public String getEmployerEmployeeById(
            Model model,
            @SessionAttribute(name = "user") User user,
            @RequestParam(name = "id", required = false) int id
    ) {
        var currentUser = userService.findByName(user.getUsername());
        if (currentUser.isPresent()) {
            if (userService.isAdmin(currentUser.get())) {
                var ee = employerEmployeeService.findById(id);
                if (ee.isPresent()) {
                    model.addAttribute("eeDto", employerEmployeeService.eeToEeDto(ee.get()));
                } else {
                    model.addAttribute("message", "Переданы неверные параметры запроса. Повторите попытку позже.");
                }
            }
        }
        return "ee/preview";
    }


    @GetMapping("/bind")
    public String bindUser(Model model) {
        var employersList = userService.findAllEmployers();
        var employeesList = userService.findAllEmployees();
        model.addAttribute("employers", employersList);
        model.addAttribute("employees", employeesList);
        return "ee/new";
    }

    @PostMapping("/bind")
    public String bindUser(
            @ModelAttribute EmployerEmployee ee,
            RedirectAttributes redirectAttributes
    ) {
        var employerEmployeeOptional = employerEmployeeService.save(ee);
        if (employerEmployeeOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка добавления. Возможно, пользователь уже был привязан.");
        } else {
            String employeeSurname = userService.findById(ee.getEmployee().getId())
                    .map(User::getSurname)
                    .orElse("Неизвестный сотрудник");
            String employerSurname = userService.findById(ee.getEmployer().getId())
                    .map(User::getSurname)
                    .orElse("Неизвестный работодатель");
            String message = String.format("Пользователь %s успешно привязан к работодателю %s.",
                    employeeSurname,
                    employerSurname
                    );
            redirectAttributes.addFlashAttribute("message", message);
        }
        return "redirect:/bindlist";
    }

}