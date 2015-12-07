package com.merritt.samples.testing.services.cc.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class WebAppInitilizer implements WebApplicationInitializer
{
    public void onStartup( final ServletContext servletContext ) throws ServletException
    {
        final AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
        root.scan( "com.merritt.samples.testing.services.cc.config" );
        addActiveProfile( root.getEnvironment(), "production" );
        servletContext.addListener( new ContextLoaderListener( root ) );
    }
    
    private void addActiveProfile( final ConfigurableEnvironment configurableEnvironment, final String profileToAdd )
    {
        configurableEnvironment.addActiveProfile( profileToAdd );
    }
}