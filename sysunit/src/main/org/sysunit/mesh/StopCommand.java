package org.sysunit.mesh;

public class StopCommand
    extends Command
{
    public void execute(final Node node)
        throws Exception
    {
        Thread thr = new Thread()
            {
                public void run()
                {
                    try
                    {
                        node.stop();
                    }
                    catch (InterruptedException e)
                    {
                        
                    }
                }
            };

        thr.start();
    }
}
