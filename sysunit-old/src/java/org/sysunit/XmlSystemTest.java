package org.sysunit;

import org.sysunit.util.MultiThrowable;

import junit.framework.Test;
import junit.framework.TestResult;

public class XmlSystemTest
    implements Test {

    private String xmlPath;

    public XmlSystemTest(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    public int countTestCases() {
        return 1;
    }

    public String getXmlPath() {
        return this.xmlPath;
    }

    public String getName() {
        return getXmlPath();
    }

    public void run(TestResult testResult) {

        testResult.startTest( this );

        try {
            org.sysunit.transport.socket.MasterNode.main( new String[] { getXmlPath() } );
        } catch (MultiThrowable mt) {
            Throwable[] throwables = mt.getThrowables();

            for ( int i = 0 ; i < throwables.length ; ++i ) {
                testResult.addError( this,
                                     throwables[i] );
            }
        } catch (Throwable e ) {
            testResult.addError( this,
                                 e );
        } finally {
            testResult.endTest( this );
        }
    }
}
