package org.sysunit.testmesh;

import org.sysunit.mesh.NodeInfo;
import org.sysunit.model.PhysicalMachineInfo;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class TestMeshManager
{
    private static NodeInfo[] EMPTY_NODEINFO_ARRAY = new NodeInfo[0];

    private List slaveHosts;
    private Map slaveHostsByTag;
    private Map slaveHostsByJdk;

    public TestMeshManager()
    {
        this.slaveHosts = new ArrayList();
        this.slaveHostsByTag = new HashMap();
        this.slaveHostsByJdk = new HashMap();
    }
    public synchronized void addSlaveHost(NodeInfo slaveHost,
                                          PhysicalMachineInfo physicalMachineInfo)
    {
        this.slaveHosts.add( slaveHost );

        String[] tags = physicalMachineInfo.getTags();

        for ( int i = 0 ; i < tags.length ; ++i )
        {
            List hosts = (List) this.slaveHostsByTag.get( tags[ i ] );

            if ( hosts == null )
            {
                hosts = new ArrayList();
                this.slaveHostsByTag.put( tags[ i ],
                                          hosts );
            }

            hosts.add( slaveHost );
        }

        String[] jdks = physicalMachineInfo.getJdks();

        for ( int i = 0 ; i < jdks.length ; ++i )
        {
            List hosts = (List) this.slaveHostsByJdk.get( jdks[ i ] );

            if ( hosts == null )
            {
                hosts = new ArrayList();
                this.slaveHostsByJdk.put( jdks[ i ],
                                          hosts );
            }

            hosts.add( slaveHost );
        }
    }

    public synchronized NodeInfo[] getSlaveHosts()
    {
        return (NodeInfo[]) this.slaveHosts.toArray( EMPTY_NODEINFO_ARRAY );
    }

    public synchronized NodeInfo[] getSlaveHostsByTag(String tag)
    {
        if ( tag.equals( "*" ) )
        {
            return getSlaveHosts();
        }

        List matchingHosts = new ArrayList();

        List hosts = (List) this.slaveHostsByTag.get( tag );

        if ( hosts != null )
        {
            matchingHosts.addAll( hosts );
        }

        hosts = (List) this.slaveHostsByTag.get( "*" );

        if ( hosts != null )
        {
            matchingHosts.addAll( hosts );
        }

        return (NodeInfo[]) matchingHosts.toArray( EMPTY_NODEINFO_ARRAY );
    }

    public synchronized NodeInfo[] getSlaveHostsByJdk(String jdk)
    {
        if ( jdk.equals( "*" ) )
        {
            return getSlaveHosts();
        }

        List matchingHosts = new ArrayList();
        
        List hosts = (List) this.slaveHostsByJdk.get( jdk );

        if ( hosts != null )
        {
            matchingHosts.addAll( hosts );
        }

        hosts = (List) this.slaveHostsByJdk.get( "*" );

        if ( hosts != null )
        {
            matchingHosts.addAll( hosts );
        }

        return (NodeInfo[]) matchingHosts.toArray( EMPTY_NODEINFO_ARRAY );
    }

    public synchronized NodeInfo[] getSlaveHostsByTagAndJdk(String tag,
                                                            String jdk)
    {
        if ( tag.equals( "*" )
             &&
             jdk.equals( "*" ) )
        {
            return getSlaveHosts();
        }

        NodeInfo[] tags = getSlaveHostsByTag( tag );
        NodeInfo[] jdks = getSlaveHostsByJdk( jdk );

        if ( tags.length == 0
             ||
             jdks.length == 0 )
        {
            return EMPTY_NODEINFO_ARRAY;
        }

        List hosts = new ArrayList();

      OUTER:
        for ( int t = 0 ; t < tags.length ; ++t )
        {
            NodeInfo node = tags[ t ];

          INNER:
            for ( int j = 0 ; j < jdks.length ; ++j )
            {
                if ( node.equals( jdks[ j ] ) )
                {
                    hosts.add( node );
                    break INNER;
                }
            }
        }

        return (NodeInfo[]) hosts.toArray( EMPTY_NODEINFO_ARRAY );
    }
}
