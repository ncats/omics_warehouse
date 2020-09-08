package gov.nih.ncats.omics.warehouse;

import javax.servlet.Filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;


/**
 * Launcher for main SpringApplication processing thread
 * 
 * @author braistedjc
 *
 */
@SpringBootApplication
@ComponentScan(basePackages={"gov.nih.ncats.omics.warehouse"})
public class OmicsApp {

    public static void main(String[] args) {
        SpringApplication.run(OmicsApp.class, args);
    }
    
    /**
     * Register the {@link OpenEntityManagerInViewFilter} so that the
     * GraphQL-Servlet can handle lazy loads during execution.
     *
     * @return
     */
    @Bean
    public Filter OpenFilter() {
      return new OpenEntityManagerInViewFilter();
    }
}
