package com.merritt.commons.testing.junit.extensions;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class InvokeMethodWithParameters extends Statement
{
    private final FrameworkMethod fTestMethod;
    private final Object fTarget;
    private final Object[] fParams;
    
    public InvokeMethodWithParameters( final FrameworkMethod testMethod, final Object target, final Object... params )
    {
        fTestMethod = testMethod;
        fTarget = target;
        fParams = params;
    }
    
    @Override
    public void evaluate() throws Throwable
    {
        fTestMethod.invokeExplosively( fTarget, fParams );
    }
}
