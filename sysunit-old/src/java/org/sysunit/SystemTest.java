package org.sysunit;

import org.sysunit.mesh.MeshManager;
import org.sysunit.mesh.transport.Transport;
import org.sysunit.mesh.transport.TransportFactory;
import org.sysunit.mesh.transport.TransportException;
import org.sysunit.model.TestInfo;

import junit.framework.Test;
import junit.framework.TestResult;

import java.io.IOException;

public class SystemTest
    implements Test {

    private TestInfo testInfo;
    private Transport transport;
    private MeshManager meshManager;

    public SystemTest(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    public TestInfo getTestInfo() {
        return this.testInfo;
    }

    public int countTestCases() {
        return 1;
    }

    public void run(TestResult testResult) {

        testResult.startTest( this );

        try {
            initializeTransport();
            initializeMeshManager();
            getMeshManager().initializeTest();
            getMeshManager().startTest();
            getMeshManager().waitFor();
            Throwable[] errors = getMeshManager().collectErrors();
        } catch (Exception e) {
            testResult.addError( this,
                                 e );
        } finally {
            getMeshManager().cleanUp();
            testResult.endTest( this );
        }
    }

    protected void initializeTransport()
        throws IOException, TransportException {
        this.transport = TransportFactory.getInstance().initializeTransport();

    }

    protected void initializeMeshManager()
        throws IOException, TransportException {
        this.meshManager = new MeshManager( getTransport(),
                                            getTestInfo() );
    }

    protected Transport getTransport() {
        return this.transport;
    }

    protected MeshManager getMeshManager() {
        return this.meshManager;
    }
}
