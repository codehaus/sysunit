package org.sysunit;

/**
 * Abstract <code>SynchronizableTBean</code> base class.
 *
 * <p>
 * This abstract base class provides management methods for
 * <code>TBeanSynchronizer</code> control.
 * </p>
 *
 * @see #setSynchronizer
 * @see #getSynchronizer
 * @see #sync
 *
 * @author Bob McWhirter
 */
public abstract class AbstractSynchronizableTBean
    extends AbstractTBean
    implements SynchronizableTBean
{

    // ----------------------------------------------------------------------
    //     Instance members
    // ----------------------------------------------------------------------

    /** The synchronizer. */
    private TBeanSynchronizer synchronizer;

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     */
    public AbstractSynchronizableTBean()
    {
        // intentionally left blank
    }

    // ----------------------------------------------------------------------
    //     Instance methods
    // ----------------------------------------------------------------------

    /**
     * @see SynchronizableTBean
     */
    public void setSynchronizer(TBeanSynchronizer synchronizer)
    {
        this.synchronizer = synchronizer;
    }

    /**
     * Retrieve the <code>TBeanSynchronizer</code>.
     *
     *  @return The synchronizer.
     */
    public TBeanSynchronizer getSynchronizer()
    {
        return this.synchronizer;
    }

    /**
     * Synchronize at a syncpoint.
     *
     * <p>
     * Synchronize at a named sync-point by blocking
     * until all other synchronizable TBeans have synchronized
     * or terminated.
     * </p>
     *
     * @param syncPoint The sync-point id.
     *
     * @throws SynchronizationException If an error occurs while
     *         blocking for synchronization.
     * @throws InterruptedException If an interrupted signal is
     *         received while blocking for synchronization.
     */
    protected void sync(String syncPoint)
        throws SynchronizationException, InterruptedException
    {
        getSynchronizer().sync( syncPoint );
    }
}
