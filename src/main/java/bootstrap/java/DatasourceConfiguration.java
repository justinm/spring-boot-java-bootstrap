package bootstrap.java;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories
public class DatasourceConfiguration {
  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource")
  public DataSourceProperties dataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Primary
  @Profile( "!test" )
  public DataSource dataSource( DataSourceProperties properties ) {
    return properties.initializeDataSourceBuilder().type( HikariDataSource.class ).build();
  }

  @Bean
  public EntityManagerFactory entityManagerFactory( DataSource ds, @Value( "${spring.jpa.packages-to-scan}" ) String packagesToScan ) {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl( true );

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter( vendorAdapter );
    factory.setPackagesToScan( packagesToScan );
    factory.setDataSource( ds );
    factory.afterPropertiesSet();

    return factory.getObject();
  }
}
