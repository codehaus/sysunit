package org.sysunit;

import junit.framework.TestSuite;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Map;
import java.util.Iterator;

public class SumoSystemTestSuite
    extends TestSuite {

    public static final String SYSTEM_TESTS_LIST_PROPERTY = "org.sysunit.sumo.test-list";
    public static final String DEFAULT_SYSTEM_TESTS_LIST = "sysunit-tests.properties";

    public static SumoSystemTestSuite newSuite(String testListPath)
        throws Exception {

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        if ( cl == null ) {
            cl = SumoSystemTestSuite.class.getClassLoader();
        }

        InputStream testListIn = testListIn = cl.getSystemResourceAsStream( testListPath );
        
        if ( testListIn == null ) {
            // attempt as a file path:
            try {
                testListIn = new FileInputStream( testListPath );
            } catch (FileNotFoundException e) {
                // swallow
            }
        }

        if ( testListIn == null ) {
            // give up
            throw new SysUnitException( "system test list not found: " + testListPath );
        }

        try {
            return newSuite( testListIn );
        } finally {
            testListIn.close();
        }
    }

    public static SumoSystemTestSuite newSuite(InputStream testListIn)
        throws Exception {

        Properties testList = new Properties();
        testList.load( testListIn );
        return newSuite( testList );
    }

    public static SumoSystemTestSuite newSuite(Map testList)
        throws Exception {

        SumoSystemTestSuite testSuite = new SumoSystemTestSuite();

        for ( Iterator xmlPaths = testList.keySet().iterator();
              xmlPaths.hasNext(); ) {
            String xmlPath = (String) xmlPaths.next();

            if ( isEnabled( (String) testList.get( xmlPath ) ) ) {
                testSuite.addTest( xmlPath );
            }
        }

        return testSuite;
    }

    public static boolean isEnabled(String value) {
        if ( value == null
             ||
             value.equals( "" )
             ||
             value.equals( "enabled" )
             ||
             value.equals( "on" )
             ||
             value.equals( "yes" )
             ||
             value.equals( "true" ) ) {
            return true;
        }

        return false;
    }

    public static TestSuite suite()
        throws Exception {
        String testsList = System.getProperty( SYSTEM_TESTS_LIST_PROPERTY );
        
        if ( testsList == null
             ||
             testsList.trim().equals( "" ) ) {
            testsList = DEFAULT_SYSTEM_TESTS_LIST;
        }

        return newSuite( DEFAULT_SYSTEM_TESTS_LIST );
    }

    public SumoSystemTestSuite() {

    }

    public SumoSystemTestSuite(String name) {
        super( name );
    }

    public void addTest(String xmlPath) {
        addTest( new XmlSystemTest( xmlPath ) );
    }
}
