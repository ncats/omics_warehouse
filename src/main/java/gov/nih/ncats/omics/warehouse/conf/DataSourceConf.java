package gov.nih.ncats.omics.warehouse.conf;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import gov.nih.ncats.omics.warehouse.service.OmicsService;

/**
 * DataSourceConf peformes dynamic data source configuration during application launch
 * @author braistedjc
 *
 */
@SpringBootConfiguration
@EnableJpaRepositories(basePackages= {"gov.nih.ncats.omics.warehouse.repository"}, entityManagerFactoryRef = "entityManagerFactory")
public class DataSourceConf {
	
	/**
	 * DataSource construction method
	 * @param jdbcUrl
	 * @param driverName
	 * @param userName
	 * @param password
	 * @return
	 */
	@Bean
	public DataSource dataSource(
			@Value("${spring.datasource.url}") String jdbcUrl,
			@Value("${spring.datasource.driver-class-name}") String driverName,
			@Value("${spring.datasource.username}") String userName,
			@Value("${spring.datasource.password}") String password
			) {
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(driverName);
		dataSourceBuilder.url(jdbcUrl);
		dataSourceBuilder.username(userName);
		dataSourceBuilder.password(password);
		return dataSourceBuilder.build();
	}
	
	
	@Bean (name = "jpaVendorAdapter")
	public JpaVendorAdapter jpaVenderAdapter() {
		HibernateJpaVendorAdapter jpa = new HibernateJpaVendorAdapter();
		return jpa;
	}
	
	/**
	 * Entity manager factory method
	 * @param dataSource
	 * @param jpaVendorAdapter
	 * @return
	 */
	@Bean (name = "entityManagerFactory")
	public EntityManagerFactory entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter)
	{
	    LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
	    lef.setDataSource(dataSource);
	    lef.setJpaVendorAdapter(jpaVendorAdapter);
	    lef.setPackagesToScan("gov.nih.ncats.omics.warehouse");
	    lef.afterPropertiesSet(); // It will initialize EntityManagerFactory object otherwise below will return null
	    return lef.getObject();
	}
	
	/**
	 * JPA transaction manager supporting server instantiation
	 * @param entityManagerFactory
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(type = "JpaTransactionManager")
	JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) 
	{
	    JpaTransactionManager transactionManager = new JpaTransactionManager();
	    transactionManager.setEntityManagerFactory(entityManagerFactory);
	    return transactionManager;
	}
	
	/**
	 * Primary OmicsService factory method to support @Autowired injection
	 * @return
	 */
	@Bean
	public OmicsService getOmicsService() {
		OmicsService srv = new OmicsService();
		return srv;
	}
	
}

