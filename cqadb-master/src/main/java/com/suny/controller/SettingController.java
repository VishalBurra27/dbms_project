package com.suny.controller;

import com.suny.service.WendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by admin on 23-2-1.10:44 p.m.
 */
@Controller
public class SettingController {

    private final WendaService wendaService;

    @Autowired
    public SettingController(WendaService wendaService) {
        this.wendaService = wendaService;
    }

    @RequestMapping(path = {"/setting"}, method = {RequestMethod.GET})
    @ResponseBody
    public String setting(HttpSession httpSession) {
        return "Setting OK ." + wendaService.getMessage(1);
    }

}















