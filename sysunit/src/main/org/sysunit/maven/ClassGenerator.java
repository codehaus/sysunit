package org.sysunit.maven;

import java.io.File;
import java.util.LinkedList;

public abstract class ClassGenerator
{
    private String baseDir;
    private String outputDir;
    private String extension;

    public ClassGenerator(String extension)
    {
        this.extension = extension;
    }

    public void setBaseDir(String baseDir)
    {
        this.baseDir = baseDir;
    }

    public String getBaseDir()
    {
        return this.baseDir;
    }

    public void setOutputDir(String outputDir)
    {
        this.outputDir = outputDir;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    public void execute()
        throws Exception
    {
        File base = new File( this.baseDir );

        LinkedList dirStack = new LinkedList();

        dirStack.addLast( base );

        while ( ! dirStack.isEmpty() )
        {
            File curDir = (File) dirStack.removeFirst();

            String dirPrefix = curDir.getPath().substring( this.baseDir.length() );
            if ( dirPrefix.startsWith( "/" )
                 ||
                 dirPrefix.startsWith( "\\" ) )
            {
                dirPrefix = dirPrefix.substring( 1 );
            }

            File[] contents = curDir.listFiles();

            for ( int i = 0 ; i < contents.length ; ++i )
            {
                if ( contents[ i ].isDirectory() )
                {
                    dirStack.addLast( contents[ i ] );
                }
                else
                {
                    if ( contents[ i ].getName().endsWith( "." + this.extension ) )
                    {
                        String fileName  = contents[ i ].getName();
                        String className = fileName.substring( 0, fileName.lastIndexOf( '.' ) );

                        String realClassName = className.replace( '.', '_' );
                        realClassName = realClassName.replace( '-', '_' );

                        File destination = new File ( new File( this.outputDir,
                                                                dirPrefix ),
                                                      realClassName + ".java" );

                        String packageName = dirPrefix.replace( File.separatorChar,
                                                                '.' );

                        if ( ! realClassName.equals( className ) )
                        {
                            System.err.println( "    [sysunit] warning: " + contents[ i ].getPath() + " generated to class " + packageName + "." + realClassName );
                        }


                        if ( ! destination.exists()
                             ||
                             destination.lastModified() > contents[ i ].lastModified() )
                        {
                            destination.getParentFile().mkdirs();
                            generateClass( packageName,
                                           realClassName,
                                           contents[ i ],
                                           destination );
                        }
                    }
                }
            }
        }
    }

    public static String getPath(File file)
    {
        String path = file.getPath();
        StringBuffer newPath = new StringBuffer();

        int cur      = -1;
        int slashLoc = -1;

        while ( ( slashLoc = path.indexOf( '\\',
                                           cur + 1 ) ) >= 0 )
        {
            newPath.append( path.substring( cur + 1,
                                            slashLoc ) );
            newPath.append( "\\\\" );

            cur = slashLoc;
        }

        if ( slashLoc < 0 )
        {
            newPath.append( path.substring( cur + 1 ) );
        }

        return newPath.toString();
    }

    public abstract void generateClass(String packageName,
                                       String className,
                                       File source,
                                       File destination)
        throws Exception;
        
}
