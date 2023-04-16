package petpalooza.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import petpalooza.Repositories.UserRepository;
import petpalooza.security.jwt.JwtAuthenticationFilter;
import petpalooza.security.jwt.JwtAuthorizationFilter;
import petpalooza.security.predifineInterfaces.UserPrincipalDetailsService;

@Configuration
@EnableWebSecurity

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(CorsConf.class)
public class SecurityConfiguration  {

    private UserRepository userRepository;
    // private BasicAuthenticationEntryPoint basicAuthenticationEntryPoint;
    private UserPrincipalDetailsService userPrincipalDetailsService;

    public SecurityConfiguration(UserPrincipalDetailsService userPrincipalDetailsService , UserRepository userRepository /*,BasicAuthenticationEntryPoint basicAuthenticationEntryPoint*/) {
        this.userPrincipalDetailsService = userPrincipalDetailsService;
        this.userRepository= userRepository;
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
//    @Bean
//    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
//        //   to activate the security to activate it comment from line 49 ---->68
//        //to disactivate security decomment the line bellow
//
////                 return http.csrf().disable().authorizeRequests().anyRequest().permitAll().and().build();
//
//        return
//                http
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                //remove csrf and state in session because jwt  do not need  them
//
//                .and()
//                .addFilter(new JwtAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class))))
//                .addFilter(new JwtAuthorizationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), this.userRepository))
//                .authorizeRequests()
//                .antMatchers("/login").permitAll().
//                        antMatchers("/public/user/*").permitAll().
//                antMatchers("/api/test/user", "/logout").authenticated().
////                .antMatchers("/api/test/manager").hasRole("MANAGEMENT").
//                antMatchers("/api/test/admin").hasRole("ADMIN").
//                antMatchers("/api/test/mod").hasRole("MANAGER").
//                and()
//                .build()
//
//                ;
//
//
//
//    }

    //  "/path/**"
    //the order of antMatchers is so important for example if i put  anyRequest().permetAll() at the beginning of the chain



    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // Disable CSRF protection
        http.csrf().disable();
        http.cors().disable();

        // Allow CORS for the /login endpoint
        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/enter/enter").permitAll();


        // Set up JWT authentication and authorization filters
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class))))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), this.userRepository))
                .authorizeRequests()
                .antMatchers("/public/user/*").permitAll()
                .antMatchers("/api/test/user", "/logout").authenticated()
                .antMatchers("/api/test/admin").hasRole("ADMIN")
                .antMatchers("/api/test/mod").hasRole("MANAGER");

        // Return the SecurityFilterChain
        return http.build();
    }



    @Bean
    DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(this.userPrincipalDetailsService);

        return daoAuthenticationProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



    //Enable SSL/HTTPS
    /*
    steps:
     1)certificate (self signed or buy
     2) Modify app.properties
     3)add @bean for ServletWebServerFactory(reddirect all http trafic to https)
     .\keytool -genkey -alias bootsecurity -storetype PKCS12 -keyalg RSA -Keysize 2048 -keystore bootsecurity.p12 -validity 3650
     */
}