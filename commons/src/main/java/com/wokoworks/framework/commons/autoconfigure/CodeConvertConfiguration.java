package com.wokoworks.framework.commons.autoconfigure;

import com.wokoworks.framework.commons.CodeConvertFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 0x0001
 */
@Configuration
public class CodeConvertConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CodeConvertFactory codeConvertFactory() {
        return new CodeConvertFactory();
    }

    @Configuration
    public static class CodeConvertBeanPostProcessor implements BeanPostProcessor {
        @Autowired
        private CodeConvertFactory factory;

        @Override
        @SuppressWarnings("unchecked")
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof CodeConvertFactory.CodeConvert) {
                factory.register(((CodeConvertFactory.CodeConvert) bean));
            }
            return bean;
        }
    }
}
