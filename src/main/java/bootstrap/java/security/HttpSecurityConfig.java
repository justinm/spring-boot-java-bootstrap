package bootstrap.java.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
public class HttpSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserDetailsService _userDetailsService;

  @Autowired
  private PasswordEncoder _passwordEncoder;

  @Autowired
  private RestAuthenticationEntryPoint _restAuthenticationEntryPoint;


  @Override
  protected void configure( HttpSecurity http ) throws Exception {
    http
      .sessionManagement()
        .sessionFixation()
        .migrateSession()
        .and()
      .userDetailsService( _userDetailsService )
      .exceptionHandling()
        .authenticationEntryPoint( _restAuthenticationEntryPoint )
        .and()
      .anonymous()
        .and()
      .httpBasic()
        .realmName( "Application" )
        .and()
      .authorizeRequests()
        .antMatchers( "/apis/**" ).authenticated()
        .anyRequest().permitAll()
        .and()
      .formLogin()
        .loginPage( "/login" ).permitAll()
        .and()
      .logout()
        .logoutUrl( "/logout" ).permitAll()
        .and()
      .csrf()
        .ignoringAntMatchers( "/apis/**" )
        .disable();
  }

  @Override
  protected void configure( AuthenticationManagerBuilder auth ) throws Exception {
    auth
      .userDetailsService( _userDetailsService )
      .passwordEncoder( _passwordEncoder );
  }

  @Bean
  public DaoAuthenticationProvider authProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

    authProvider.setUserDetailsService( _userDetailsService );
    authProvider.setPasswordEncoder( _passwordEncoder );

    return authProvider;
  }

  @Override
  @Bean
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
    return new RestAuthenticationEntryPoint();
  }
}
