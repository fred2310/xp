package com.enonic.gradle;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.tasks.bundling.Jar;

import aQute.bnd.gradle.BundleTaskConvention;

public final class OsgiConfigurator
{
    private final Project project;

    private BundleTaskConvention ext;

    public OsgiConfigurator( final Project project )
    {
        this.project = project;
    }

    private void addLibraryConfig()
    {
        final Configuration libConfig = this.project.getConfigurations().create( "include", conf -> {
            conf.setTransitive( true );
        } );

        this.project.getConfigurations().getByName( "compile" ).extendsFrom( libConfig );
    }

    private void afterConfigure()
    {
        final Jar jar = (Jar) this.project.getTasks().getByName( "jar" );
        this.ext = (BundleTaskConvention) jar.getConvention().getPlugins().get( "bundle" );

        final Configuration libConfig = this.project.getConfigurations().getByName( "include" );
        instruction( "Bundle-ClassPath", getBundleClassPath( libConfig ) );
        instruction( "Include-Resource", getIncludeResource( libConfig ) );
    }

    private void instruction( final String name, final Object value )
    {
        if ( value != null )
        {
            final Map<String, String> instructions = new HashMap<>();
            instructions.put( name, value.toString() );
            this.ext.bnd( instructions );
        }
    }

    private String getBundleClassPath( final Configuration config )
    {
        final StringBuilder str = new StringBuilder( "." );
        for ( final ResolvedArtifact artifact : config.getResolvedConfiguration().getResolvedArtifacts() )
        {
            str.append( ",OSGI-INF/lib/" ).append( getFileName( artifact ) );
        }

        return str.toString();
    }

    private String getIncludeResource( final Configuration config )
    {
        final StringBuilder str = new StringBuilder( "" );
        for ( final ResolvedArtifact artifact : config.getResolvedConfiguration().getResolvedArtifacts() )
        {
            final String name = getFileName( artifact );
            str.append( ",OSGI-INF/lib/" ).append( name ).append( "=" ).append( artifact.getFile().getPath() );
        }

        return str.length() > 0 ? str.substring( 1 ) : str.toString();
    }

    private String getFileName( final ResolvedArtifact artifact )
    {
        final ModuleVersionIdentifier id = artifact.getModuleVersion().getId();
        String name = id.getName() + "-" + id.getVersion();

        if ( artifact.getClassifier() != null )
        {
            name += "-" + artifact.getClassifier();
        }

        return name + "." + artifact.getExtension();
    }

    public static void configure( final Project project )
    {
        final OsgiConfigurator conf = new OsgiConfigurator( project );
        conf.addLibraryConfig();

        project.afterEvaluate( p -> {
            conf.afterConfigure();
        } );
    }
}
