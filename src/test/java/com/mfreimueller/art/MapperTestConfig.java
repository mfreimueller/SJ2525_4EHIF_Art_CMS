package com.mfreimueller.art;

import com.mfreimueller.art.mappers.SpringMapperConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(basePackageClasses = SpringMapperConfig.class)
public class MapperTestConfig {
}
