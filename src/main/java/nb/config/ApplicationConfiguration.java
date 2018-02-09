package nb.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Created by kropamorkv on 3/12/17.
 */
@Configuration
@EnableWebMvc
@ComponentScan({"nb"})
@Import(HibernateConfiguration.class)
public class ApplicationConfiguration extends WebMvcConfigurerAdapter {

/*        @Autowired
        RoleToUserProfileConverter roleToUserProfileConverter;*/

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        }

        @Bean
        public InternalResourceViewResolver setupViewResolver() {
                InternalResourceViewResolver resolver = new InternalResourceViewResolver();
                resolver.setPrefix("/WEB-INF/views/");
                resolver.setSuffix(".jsp");
                resolver.setViewClass(JstlView.class);

                return resolver;
        }

    @Bean
    public MultipartResolver multipartResolver() {
        org.springframework.web.multipart.commons.CommonsMultipartResolver multipartResolver = new org.springframework.web.multipart.commons.CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(10000000);
        return multipartResolver;
    }
        /**
         * Configure Converter to be used.
         * In our example, we need a converter to convert string values[Roles] to UserProfiles in newUser.jsp
         */
       /* @Override
        public void addFormatters(FormatterRegistry registry) {
                registry.addConverter(roleToUserProfileConverter);
        }*/

        /**
         * Configure MessageSource to lookup any validation/error message in internationalized property files
         */
        @Bean
        public MessageSource messageSource() {
                ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
                messageSource.setBasename("messages");
                return messageSource;
        }

        /**Optional. It's only required when handling '.' in @PathVariables which otherwise ignore everything after last '.' in @PathVaidables argument.
         * It's a known bug in Spring [https://jira.spring.io/browse/SPR-6164], still present in Spring 4.1.7.
         * This is a workaround for this issue.
         */
        @Override
        public void configurePathMatch(PathMatchConfigurer matcher) {
                matcher.setUseRegisteredSuffixPatternMatch(true);
        }

}
