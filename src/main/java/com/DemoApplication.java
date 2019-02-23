package com;

import com.leo.interceptor.KuaYuFilter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
@RestController
@EnableScheduling
// extends SpringBootServletInitializer
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//		return application.sources(DemoApplication.class);
//	}

	@RequestMapping("/leo")
	public String greeting() {
		return "Hello World!1";
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/admin/leo/*").allowedOrigins("http://localhost:9528");
			}
		};
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}
			// 打开浏览器，输入地址。
			try {
				Runtime.getRuntime().exec(
						"cmd   /c   start   http://localhost:4099/build/index.html");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			---------------------
					作者：爱的叹息
			来源：CSDN
			原文：https://blog.csdn.net/zp357252539/article/details/77896257/
			版权声明：本文为博主原创文章，转载请附上博文链接！*/
		};
	}

	@Bean
	public FilterRegistrationBean registerFilter() {
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.addUrlPatterns("/*");
		bean.setFilter(new KuaYuFilter());
		// 过滤顺序，从小到大依次过滤
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

		return bean;
	}
}
