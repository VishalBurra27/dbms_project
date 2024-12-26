package com.suny.configuration;

import com.suny.interceptor.LoginRequiredInterceptor;
import com.suny.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * register interceptor
 * Created by admin on 23-2-16.9:12 pm
 */
@Component
public class CqadbWebConfiguration extends WebMvcConfigurerAdapter {

    private final PassportInterceptor passportInterceptor;

    private final LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    public CqadbWebConfiguration(LoginRequiredInterceptor loginRequiredInterceptor, PassportInterceptor passportInterceptor) {
        this.loginRequiredInterceptor = loginRequiredInterceptor;
        this.passportInterceptor = passportInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/**");
        super.addInterceptors(registry);
    }
}
