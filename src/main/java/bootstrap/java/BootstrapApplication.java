package bootstrap.java;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@SpringBootApplication
@EnableAsync
public class BootstrapApplication extends SpringBootServletInitializer {
  private static final Logger LOGGER = LogManager.getLogger( BootstrapApplication.class );
  public static void main( String[] args ) {
    SpringApplication.run( BootstrapApplication.class, args );
  }

  @Bean
  public HandlerExceptionResolver handlerExceptionResolver() {
    return ( request, response, handler, ex ) -> {
      LOGGER.error( ex );
      return new ModelAndView();
    };
  }

  @Bean
  @Description("Thymeleaf template resolver serving HTML 5")
  public ClassLoaderTemplateResolver templateResolver() {
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

    templateResolver.setPrefix( "templates/" );
    templateResolver.setCacheable( false );
    templateResolver.setSuffix( ".html" );
    templateResolver.setCharacterEncoding( "UTF-8" );

    return templateResolver;
  }

  @Bean
  public SpringTemplateEngine templateEngine( ClassLoaderTemplateResolver templateResolver ) {
    // SpringTemplateEngine automatically applies SpringStandardDialect and
    // enables Spring's own MessageSource message resolution mechanisms.
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver( templateResolver );
    // Enabling the SpringEL compiler with Spring 4.2.4 or newer can
    // speed up execution in most scenarios, but might be incompatible
    // with specific cases when expressions in one template are reused
    // across different data types, so this flag is "false" by default
    // for safer backwards compatibility.
    templateEngine.setEnableSpringELCompiler( true );

    return templateEngine;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
