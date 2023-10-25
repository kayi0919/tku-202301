package org.tku.web.config;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration  //定義此類別為配置類別
@Log4j2
public class I18nConfiguration {

    private static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";

    //外部資料注入進來
    @Value("${web.i18n.basename:classpath:i18n/application-messages}")
    private List<String> basename;

    @Value("${web.i18n.encoding:UTF-8}")
    private String encoding;

    @Bean //配置函數
    public MessageSource messageSource(){
        //ReloadableResourceBundleMessageSource 用於加載和管理國際化（i18n）消息資源文件
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        log.debug("i18n config file => {}", basename);
        messageSource.setBasenames(basename.toArray(new String[basename.size()]));
        messageSource.setDefaultEncoding(encoding);
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator(){
        //LocalValidatorFactoryBean 整合 Java Bean 驗證（Bean Validation）功能到 Spring 應用程式中
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    public LocaleResolver localeResolver(){
        //LocaleResolver 用於處理應用程式的區域設定（Locale）
        //SessionLocaleResolver允許用戶在應用程序中選擇首選的語言和區域設定，而不受客戶端瀏覽器設定的影響。
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.TAIWAN);
        localeResolver.setLocaleAttributeName("locale");
        return localeResolver;
    }
}
