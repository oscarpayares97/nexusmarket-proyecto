package application.adapters.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Registra como beans de Spring los servicios de dominio y los casos de uso,
 * sin anotar el dominio con Spring (el dominio queda puro).
 */
@Configuration
@ComponentScan(
        basePackages = {"application.domain.services", "application.adapters.useCases"},
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = ".*(Service|Guard|Loader|UseCaseImpl)$"),
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = ".*(Fake|InMemory).*"))
public class DomainBeansConfig {
}