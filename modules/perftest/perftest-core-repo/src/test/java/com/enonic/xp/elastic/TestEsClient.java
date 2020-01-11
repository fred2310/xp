package com.enonic.xp.elastic;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import com.enonic.xp.elasticsearch.client.impl.EsClient;

public final class TestEsClient
    extends EsClient
{
    public TestEsClient( String hostname )
    {
        this( hostname, 9200 );
    }

    public TestEsClient( String hostname, int port )
    {
        if ( client != null )
        {
            try
            {
                client.close();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
        client = new TestRestHighLevelClient( RestClient.builder( new HttpHost( hostname, port, "http" ) ) );
    }

    @Override
    public void close()
        throws IOException
    {
        client.close();
    }
}
