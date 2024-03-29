package com.nichoudhary.soap.webservices.soapcoursemanagement.soap;


import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.security.xwss.XwsSecurityInterceptor;
import org.springframework.ws.soap.security.xwss.callback.SimplePasswordValidationCallbackHandler;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import javax.security.auth.callback.CallbackHandler;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


//Enable Spring Web Services
// Spring Configuration
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    //Message Dispatcher Servlet
        //Application Context
        // uri -> /ws/*

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext context) {
        // Map message dispatcher servlet to this url "/ws/*"
        MessageDispatcherServlet messageDispatcherServlet = new MessageDispatcherServlet();
        messageDispatcherServlet.setApplicationContext(context);
        messageDispatcherServlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(messageDispatcherServlet, "/ws/*");
    }

    //  /ws/courses.wsdl
        // portType - CoursePort
        //Namespace -  http://in28minutes.com/courses
        // course-details.xsd

    @Bean(name = "courses")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema coursesSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        // location/ uri = /ws/courses.wsdl
        // portType - CoursePort (somehing like an interface)
        //Namespace -  http://in28minutes.com/courses
        // schema - course-details.xsd

        definition.setPortTypeName("CoursePort");
        definition.setTargetNamespace("http://in28minutes.com/courses");
        definition.setLocationUri("/ws");
        definition.setSchema(coursesSchema);
        return definition;
    }

    @Bean
    public XsdSchema coursesSchema() {
        return new SimpleXsdSchema(new ClassPathResource("course-details.xsd"));
    }

    //XwsSecurityInterceptor
        //Call back Handler -> SimplePasswordValidationCallbackHandler
        //Security Policy -> securityPolicy.xml
    //Interceptors.add -> XwsSecurityInterceptor
    @Bean
    public XwsSecurityInterceptor securityInterceptor() {
        XwsSecurityInterceptor securityInterceptor = new XwsSecurityInterceptor();
        //Callback Handler -> SimplePasswordValidationCallbackHandler
        securityInterceptor.setCallbackHandler(callbackHandler());
        //Security Policy -> securityPolicy.xml
        securityInterceptor.setPolicyConfiguration(new ClassPathResource("securityPolicy.xml"));
        return securityInterceptor;
    }

    @Bean
    public SimplePasswordValidationCallbackHandler callbackHandler() {
        SimplePasswordValidationCallbackHandler handler = new SimplePasswordValidationCallbackHandler();
        handler.setUsersMap(Collections.singletonMap("user", "password"));
        return handler;
    }

    //Interceptors.add -> XwsSecurityInterceptor
    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(securityInterceptor());
    }

}
