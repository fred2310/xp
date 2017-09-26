package com.enonic.xp.script.impl.purplejs.adapter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.purplejs.core.resource.Resource;
import io.purplejs.core.resource.ResourcePath;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

import static org.junit.Assert.*;

public class ResourceLoaderAdapterTest
{
    private ApplicationKey applicationKey;

    private ResourceService mock;

    private ResourceLoaderAdapter adapter;

    @Before
    public void setup()
    {
        this.mock = Mockito.mock( ResourceService.class );
        this.applicationKey = ApplicationKey.from( "app" );
        this.adapter = new ResourceLoaderAdapter( this.applicationKey, this.mock );
    }

    private void mockResource( final String path, final boolean exists )
    {
        final ResourceKey key = ResourceKey.from( this.applicationKey, path );

        final com.enonic.xp.resource.Resource resource = Mockito.mock( com.enonic.xp.resource.Resource.class );
        Mockito.when( resource.getKey() ).thenReturn( key );
        Mockito.when( resource.exists() ).thenReturn( exists );

        Mockito.when( this.mock.getResource( key ) ).thenReturn( resource );
    }

    @Test
    public void loadOrNull()
    {
        mockResource( "/a/c.js", true );
        final Resource resource = this.adapter.loadOrNull( ResourcePath.from( "/a/c.js" ) );
        assertNotNull( resource );
        assertEquals( "/a/c.js", resource.getPath().toString() );
    }

    @Test
    public void loadOrNull_notFound()
    {
        mockResource( "/a/b.js", false );
        final Resource resource = this.adapter.loadOrNull( ResourcePath.from( "/a/b.js" ) );
        assertNull( resource );
    }
}
