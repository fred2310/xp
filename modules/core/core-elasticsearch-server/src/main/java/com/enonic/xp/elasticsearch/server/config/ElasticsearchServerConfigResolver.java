package com.enonic.xp.elasticsearch.server.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.enonic.xp.elasticsearch.server.impl.ElasticsearchServerSettings;
import com.enonic.xp.home.HomeDir;

import static com.google.common.base.Strings.nullToEmpty;

public final class ElasticsearchServerConfigResolver
{

    private final ElasticsearchServerConfig configuration;

    public ElasticsearchServerConfigResolver( final ElasticsearchServerConfig configuration )
    {
        this.configuration = configuration;
    }

    public ElasticsearchServerSettings resolve()
    {
        final ElasticsearchConfigValidator validator = new ElasticsearchConfigValidator();
        validator.validate();

        final ElasticsearchServerSettings.Builder builder = ElasticsearchServerSettings.builder().
            pathConf( resolvePathConfDir() ).
            pathData( resolvePathDataDir() ).
            pathLogs( resolvePathLogsDir() ).
            pathRepo( resolvePathRepoDir() ).
            pathWork( resolvePathWorkDir() ).
            gatewayExpectedNodes( configuration.gateway_expectedNodes() ).
            gatewayRecoverAfterNodes( configuration.gateway_recoverAfterNodes() ).
            gatewayRecoverAfterTime( configuration.gateway_recoverAfterTime() ).
            clusterRoutingAllocationDiskThresholdEnabled( configuration.cluster_routing_allocation_disk_thresholdEnabled() ).
            clusterName( configuration.cluster_name() ).
            indexMaxResultWindow( configuration.index_maxResultWindow() ).
            httpPort( configuration.http_port() ).
            transportPort( configuration.transport_port() );

        return builder.build();
    }

    private String getPathOfDirAsString( final String value, final String defaultSubPath )
    {
        try
        {
            if ( !nullToEmpty( value ).isBlank() )
            {
                if ( Files.notExists( Paths.get( value ) ) )
                {
                    Files.createDirectories( Paths.get( value ) );
                }

                return value;
            }

            return Files.createDirectories( HomeDir.get().toFile().toPath().resolve( defaultSubPath ) ).toAbsolutePath().toString();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public String resolvePathConfDir()
    {
        final String value = configuration.path_conf();
        return getPathOfDirAsString( value, "repo/index/conf" );
    }

    public String resolvePathRepoDir()
    {
        final String value = configuration.path_repo();
        return getPathOfDirAsString( value, "snapshots" );
    }

    public String resolvePathWorkDir()
    {
        final String value = configuration.path_work();
        return getPathOfDirAsString( value, "repo/index/work" );
    }

    public String resolvePathDataDir()
    {
        final String value = configuration.path_data();
        return getPathOfDirAsString( value, "repo/index/data" );
    }

    public String resolvePathLogsDir()
    {
        final String value = configuration.path_logs();
        return getPathOfDirAsString( value, "repo/index/logs" );
    }

    private class ElasticsearchConfigValidator
    {

        private void validate()
        {
            ensureCorrectHttpPort().
                ensureCorrectTransportPort().
                ensureCorrectClusterName().
                ensureCorrectGatewayRecoverAfterTime().
                ensureCorrectGatewayExpectedNodes().
                ensureCorrectGatewayRecoverAfterNodes().
                ensureCorrectIndexMaxResultWindow();
        }

        ElasticsearchConfigValidator ensureCorrectHttpPort()
        {
            if ( nullToEmpty( configuration.http_port() ).isBlank() )
            {
                throw new IllegalArgumentException( "The \"http.port\" parameter is required." );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectClusterName()
        {
            if ( nullToEmpty( configuration.cluster_name() ).isBlank() )
            {
                throw new IllegalArgumentException( "The \"cluster.name\" parameter is required." );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectTransportPort()
        {
            if ( nullToEmpty( configuration.transport_port() ).isBlank() )
            {
                throw new IllegalArgumentException( "The \"transport.port\" parameter is required." );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectGatewayRecoverAfterTime()
        {
            if ( nullToEmpty( configuration.gateway_recoverAfterTime() ).isBlank() )
            {
                throw new IllegalArgumentException( "The \"gateway.RecoverAfterTime\" parameter is required." );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectGatewayExpectedNodes()
        {
            if ( configuration.gateway_expectedNodes() < 0 )
            {
                throw new IllegalArgumentException( "The \"gateway.ExpectedNodes\" must be positive" );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectGatewayRecoverAfterNodes()
        {
            if ( configuration.gateway_expectedNodes() < 0 )
            {
                throw new IllegalArgumentException( "The \"gateway.RecoverAfterNodes\" must be positive" );
            }

            return this;
        }

        ElasticsearchConfigValidator ensureCorrectIndexMaxResultWindow()
        {
            if ( configuration.gateway_expectedNodes() < 0 )
            {
                throw new IllegalArgumentException( "The \"index.MaxResultWindow\" must be positive" );
            }

            return this;
        }

    }

}