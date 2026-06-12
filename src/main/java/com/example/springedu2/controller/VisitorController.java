package com.example.springedu2.controller;

import com.example.springedu2.entity.Visitor;
import com.example.springedu2.repository.VisitorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorRepository visitorRepository;

    private ModelAndView visitorView(List<Visitor> visitors, String message) {
        ModelAndView mav = new ModelAndView("visitorView");
        mav.addObject("list", visitors);
        mav.addObject("memo", message);
        return mav;
    }

    @GetMapping("/vlist")
    public ModelAndView list(){
        List<Visitor> visitors = visitorRepository.findAll();
        return visitorView(visitors,null);
    }

    @GetMapping("/vsearch")
    public ModelAndView search(@RequestParam(defaultValue = "") String key){
        List<Visitor> visitors = key.isBlank()
                ? visitorRepository.findAll()
                : visitorRepository.findByMemoContainingIgnoreCaseOrderByIdDesc(key);
        return visitorView(visitors, "메인페이지로 돌아가기");
    }

    @PostMapping("/vinsert")
    @Transactional
    public String insert(@Valid Visitor visitor, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("msg", "이름과 내용을 모두 입력하세요");
            return "visitorView";
        }
        visitorRepository.save(visitor);
        return "redirect:/vlist";
    }

    @GetMapping(value = "/one", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<Visitor> one(@RequestParam Integer id){
        return visitorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
