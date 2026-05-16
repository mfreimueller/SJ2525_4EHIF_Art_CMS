package com.mfreimueller.art.presentation.web;

import com.mfreimueller.art.domain.VisitHistory;
import com.mfreimueller.art.service.VisitHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/web/visit-histories")
public class VisitHistoryWebController {

    private final VisitHistoryService service;

    @GetMapping
    public String list(Model model) {
        var histories = service.getVisitHistories(Pageable.unpaged()).getContent();
        model.addAttribute("histories", histories);
        return "visit-history/list";
    }

    @GetMapping("/{key}")
    public String detail(@PathVariable Long key, Model model) {
        var history = service.getByReference(new VisitHistory.VisitHistoryId(key));
        model.addAttribute("history", history);
        return "visit-history/detail";
    }
}
