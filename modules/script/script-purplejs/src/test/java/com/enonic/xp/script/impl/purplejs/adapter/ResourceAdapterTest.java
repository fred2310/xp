package com.enonic.xp.script.impl.purplejs.adapter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import io.purplejs.core.resource.ResourcePath;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

import static org.junit.Assert.*;

public class ResourceAdapterTest
{
    private Resource mock;

    private ResourceAdapter adapter;

    @Before
    public void setup()
    {
        this.mock = Mockito.mock( Resource.class );
        this.adapter = new ResourceAdapter( this.mock );
    }

    @Test
    public void getPath()
    {
        Mockito.when( this.mock.getKey() ).thenReturn( ResourceKey.from( "app:/a/b.js" ) );

        final ResourcePath path = this.adapter.getPath();
        assertNotNull( path );
        assertEquals( "/a/b.js", path.toString() );
    }

    @Test
    public void getSize()
    {
        Mockito.when( this.mock.getSize() ).thenReturn( 123L );

        final long size = this.adapter.getSize();
        assertEquals( 123L, size );
    }

    @Test
    public void getLastModified()
    {
        Mockito.when( this.mock.getTimestamp() ).thenReturn( 1234L );

        final long lastModified = this.adapter.getLastModified();
        assertEquals( 1234L, lastModified );
    }

    @Test
    public void getBytes()
    {
        final ByteSource expected = ByteSource.empty();
        Mockito.when( this.mock.getBytes() ).thenReturn( expected );

        final ByteSource bytes = this.adapter.getBytes();
        assertSame( expected, bytes );
    }
}
