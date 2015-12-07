package com.merritt.commons.testing.junit.extensions;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * <p>
 * The custom runner <code>Parameterized</code> implements parameterized tests.
 * When running a parameterized test class, instances are created for the
 * cross-product of the test methods and the test data elements.
 * </p>
 * 
 * For example, to test a Fibonacci function, write:
 * 
 * <pre>
 * &#064;RunWith(Parameterized.class)
 * public class FibonacciTest {
 * 	&#064;Parameters
 * 	public static List&lt;Object[]&gt; data() {
 * 		return Arrays.asList(new Object[][] {
 * 				Fibonacci,
 * 				{ { 0, 0 }, { 1, 1 }, { 2, 1 }, { 3, 2 }, { 4, 3 }, { 5, 5 },
 * 						{ 6, 8 } } });
 * 	}
 * 
 * 	private int fInput;
 * 
 * 	private int fExpected;
 * 
 * 	public FibonacciTest(int input, int expected) {
 * 		fInput= input;
 * 		fExpected= expected;
 * 	}
 * 
 * 	&#064;Test
 * 	public void test() {
 * 		assertEquals(fExpected, Fibonacci.compute(fInput));
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * Each instance of <code>FibonacciTest</code> will be constructed using the
 * two-argument constructor and the data values in the
 * <code>&#064;Parameters</code> method.
 * </p>
 */
public class Parameterized extends Suite
{
    /**
     * Annotation for a method which provides parameters to be injected into the
     * test class constructor by <code>Parameterized</code>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface Parameters
    {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExcelDataSource
    {
        String path();
        
        String sheetName() default "Sheet1";
    }
    
    private class TestClassRunnerForParameters extends BlockJUnit4ClassRunner
    {
        private final int fParameterSetNumber;
        
        private final Map<String, String> fParameterList;
        
        private final String fMethodName;
        
        TestClassRunnerForParameters( Class<?> type, Map<String, String> parameterList, int i, String methodName ) throws InitializationError
        {
            super( type );
            fParameterList = parameterList;
            fParameterSetNumber = i;
            fMethodName = methodName;
        }
        
        @Override
        public Object createTest() throws Exception
        {
            return getTestClass().getOnlyConstructor().newInstance();
        }
        
        //        private Object[] computeParams() throws Exception
        //        {
        //            try
        //            {
        //                return fParameterList.get( fParameterSetNumber );
        //            }
        //            catch( ClassCastException e )
        //            {
        //                throw new Exception( String.format( "%s.%s() must return a Collection of arrays.", getTestClass().getName(), getParametersMethod( getTestClass() ).getName() ) );
        //            }
        //        }
        
        @Override
        protected List<FrameworkMethod> computeTestMethods()
        {
            List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods( Test.class );
            for( FrameworkMethod method : methods )
            {
                if( method.getName() == fMethodName )
                {
                    return new ArrayList<FrameworkMethod>( Arrays.asList( new FrameworkMethod[] { method } ) );
                }
            }
            return getTestClass().getAnnotatedMethods( Test.class );
        }
        
        @Override
        protected String getName()
        {
            return String.format( "%s", fMethodName );
        }
        
        @Override
        protected String testName( final FrameworkMethod method )
        {
            //return UUID.randomUUID().toString();
            return String.format( "%s[%s]", method.getName(), fParameterSetNumber );
        }
        
        @Override
        protected void validateConstructor( List<Throwable> errors )
        {
            validateOnlyOneConstructor( errors );
        }
        
        @Override
        protected Statement classBlock( RunNotifier notifier )
        {
            return childrenInvoker( notifier );
        }
        
        @Override
        protected Statement methodInvoker( FrameworkMethod method, Object test )
        {
            Map<String, String> params = null;
            try
            {
                params = fParameterList;
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
            return new InvokeMethodWithParameters( method, test, params );
        }
        
        @Override
        protected void validateTestMethods( List<Throwable> errors )
        {
            
        }
        
    }
    
    private final ArrayList<Runner> runners = new ArrayList<Runner>();
    
    /**
     * Only called reflectively. Do not use programmatically.
     */
    public Parameterized( Class<?> klass ) throws Throwable
    {
        super( klass, Collections.<Runner> emptyList() );
        //List<Object[]> parametersList = getParametersList( getTestClass() );
        List<FrameworkMethod> methods = getTestMethods( getTestClass() );
        
        for( FrameworkMethod method : methods )
        {
            List<Map<String, String>> parametersList = getExcelSheets( method );
            int testCase = 0;
            for( Map<String, String> parameters : parametersList )
            {
                runners.add( new TestClassRunnerForParameters( getTestClass().getJavaClass(), parameters, testCase++, method.getName() ) );
            }
            parametersList = new ArrayList<Map<String, String>>();
        }
    }
    
    @Override
    protected List<Runner> getChildren()
    {
        return runners;
    }
    
    private List<Map<String, String>> getExcelSheets( FrameworkMethod method ) throws Exception
    {
        
        ExcelDataSource excelDataSource = method.getAnnotation( ExcelDataSource.class );
        if( excelDataSource != null )
        {
            InputStream spreadsheet = new FileInputStream( excelDataSource.path() );
            return new SpreadsheetData( spreadsheet, excelDataSource.sheetName() ).getData();
        }
        else
        {
            return new ArrayList<Map<String, String>>();
        }
        
    }

    
    private List<FrameworkMethod> getTestMethods( TestClass testClass ) throws Exception
    {
        return testClass.getAnnotatedMethods( Test.class );
    }
}
