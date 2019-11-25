package com.enonic.xp.repo.impl.elasticsearch.distro;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchConfig;
import com.enonic.xp.repo.impl.elasticsearch.distro.config.ElasticsearchDownloaderConfig;

import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.ES_DIR;
import static com.enonic.xp.repo.impl.elasticsearch.distro.ElasticsearchConstants.EXTRACTED_ARCHIVE_NAME;
import static java.nio.charset.StandardCharsets.UTF_8;


public class ElasticsearchServer
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ElasticsearchServer.class );

    private Process process;

    private Thread outStreamReader;

    private final CountDownLatch startedLatch = new CountDownLatch( 1 );

    private final AtomicBoolean statedSuccessfully = new AtomicBoolean();

    private final String esJavaOpts;

    private final ElasticsearchConfig config;


    private final ElasticsearchInstaller installer;

    private ElasticsearchServer( final ElasticsearchServerBuilder builder )
    {
        this.esJavaOpts = builder.esJavaOpts;
        this.installer = new ElasticsearchInstaller( builder.downloaderConfig );
        this.config = builder.elasticsearchConfig;
    }

    public synchronized void start()
        throws InterruptedException, IOException
    {
        installer.install();
        configure();
        startElasticProcess();
        installExitHook();
        startedLatch.await();
    }

    public synchronized void stop()
    {
        try
        {
            stopElasticServer();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public boolean isStarted()
    {
        return startedLatch.getCount() == 0;
    }

    private void installExitHook()
    {
        Runtime.getRuntime().addShutdownHook( new Thread( this::stop, "ElsInstanceCleaner" ) );
    }


    public void startElasticProcess()
        throws IOException, InterruptedException
    {
        process = new ProcessBuilder(
            Path.of( installer.getInstallationDirectory().getPath(), EXTRACTED_ARCHIVE_NAME, "bin", executableFilename() ).toString() ).
            redirectErrorStream( true ).
            start();

        outStreamReader = new Thread( () -> {

            try (final BufferedReader in = new BufferedReader( new InputStreamReader( process.getInputStream() ) ))
            {
                String line;
                while ( ( line = in.readLine() ) != null )
                {
                    LOGGER.info( line );
                    if ( line.endsWith( "] started" ) )
                    {
                        statedSuccessfully.set( true );
                        startedLatch.countDown();
                    }
                    if ( Thread.interrupted() )
                    {
                        return;
                    }
                }

            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
            finally
            {
                startedLatch.countDown();
            }
        } );
        outStreamReader.start();
    }

    private void stopElasticServer()
        throws IOException, InterruptedException
    {
        LOGGER.info( "Stopping elasticsearch server..." );
        deactivate();
    }

    private void configure()
        throws IOException
    {
        if ( config != null )
        {
            File elasticsearchYml = FileUtils.getFile( ES_DIR.toFile(), EXTRACTED_ARCHIVE_NAME, "config", "elasticsearch.yml" );
            FileUtils.writeStringToFile( elasticsearchYml, config.toYaml(), UTF_8 );
        }
    }

    public static class ElasticsearchServerBuilder
    {
        private String esJavaOpts;

        private ElasticsearchDownloaderConfig downloaderConfig;

        private ElasticsearchConfig elasticsearchConfig;

        public static ElasticsearchServerBuilder builder()
        {
            return new ElasticsearchServerBuilder();
        }

        public ElasticsearchServerBuilder esJavaOpts( final String esJavaOpts )
        {
            this.esJavaOpts = esJavaOpts;
            return this;
        }

        public ElasticsearchServerBuilder downloaderConfig( final ElasticsearchDownloaderConfig downloaderConfig )
        {
            this.downloaderConfig = downloaderConfig;
            return this;
        }

        public ElasticsearchServerBuilder elasticsearchConfig( final ElasticsearchConfig elasticsearchConfig )
        {
            this.elasticsearchConfig = elasticsearchConfig;
            return this;
        }

        public ElasticsearchServer build()
        {
            return new ElasticsearchServer( this );
        }
    }

    public void deactivate()
        throws IOException, InterruptedException
    {
        startedLatch.await();
        outStreamReader.interrupt();
        process.children().forEach( ProcessHandle::destroy );
        process.destroy();
        process.waitFor( 1, TimeUnit.MINUTES );
    }

    private static String executableFilename()
    {
        return "elasticsearch" + ( isWindows() ? ".bat" : "" );
    }

    private static boolean isWindows()
    {
        return System.getProperty( "os.name" ).startsWith( "Windows" );
    }

}

