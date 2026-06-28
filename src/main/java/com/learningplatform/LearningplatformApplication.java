
package com.learningplatform;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LearningplatformApplication 
{

	public static void main(String[] args) 
	{
		
		SpringApplication.run(LearningplatformApplication.class, args);
	}
	
	@Bean
	public ModelMapper modelMapper()
	{
		
		 ModelMapper modelMapper = new ModelMapper();
		return  modelMapper;
	}

}
