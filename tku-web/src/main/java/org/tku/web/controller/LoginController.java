package org.tku.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;


@Controller
@Log4j2
public class LoginController {

    @Autowired
    LocaleResolver localeResolver;

    @GetMapping("/web/index")   //GetMapping配置 http://127.0.0.1/web/index
    public String index(@RequestParam(required = false) String locale,
                        HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isNotBlank(locale)){
            localeResolver.setLocale(request, response, new Locale(locale));
        }
        //當輸入getmapping的網址 回傳 templates裡的index.html
        return "index";
    }

    @GetMapping(value = {"/login","/"})
    public String loginPage() throws JsonProcessingException{
        return "login";
    }

}
