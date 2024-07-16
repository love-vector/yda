package ai.yda.application.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ai.yda.common.shared.filter.HttpSessionThreadLocalFilter;
import ai.yda.common.shared.service.impl.ThreadLocalSessionProvider;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<HttpSessionThreadLocalFilter> threadLocalSessionFilter(
            ThreadLocalSessionProvider sessionProvider) {
        var registrationBean = new FilterRegistrationBean<HttpSessionThreadLocalFilter>();
        registrationBean.setFilter(new HttpSessionThreadLocalFilter(sessionProvider));
        return registrationBean;
    }

    @Bean
    public ThreadLocalSessionProvider geHttpSessionThreadLocalSessionProvider() {
        return new ThreadLocalSessionProvider();
    }
}
