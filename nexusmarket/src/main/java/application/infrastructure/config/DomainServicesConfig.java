package application.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Registers every class of {@code application.domain.services} whose name ends with
 * "Service" as a Spring bean, so the domain layer stays free of Spring annotations.
 * Dependencies are injected through each service's single constructor.
 */
@Configuration
@ComponentScan(
        basePackages = "application.domain.services",
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*Service$"))
public class DomainServicesConfig {
}
