package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.domain.Visitor;
import com.mfreimueller.art.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/web/visitors")
public class VisitorWebController {

    private final VisitorService service;

    @GetMapping
    public String list(Model model) {
        var visitors = service.getVisitors(Pageable.unpaged()).getContent();
        model.addAttribute("visitors", visitors);
        return "visitor/list";
    }

    @GetMapping("/{key}")
    public String detail(@PathVariable Long key, Model model) {
        var visitor = service.getByReference(new Visitor.VisitorId(key));
        model.addAttribute("visitor", visitor);
        return "visitor/detail";
    }
}
