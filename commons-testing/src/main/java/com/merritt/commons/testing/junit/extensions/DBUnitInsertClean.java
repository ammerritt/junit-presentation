package com.merritt.commons.testing.junit.extensions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface DBUnitInsertClean
{
    String dataSource();
    
    String[] xmlFiles() default {};
    
    String xmlDirectory() default "";
    
}