package uk.gov.hmcts.reform.dev.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(){

        return new ModelMapper();
    }
}
