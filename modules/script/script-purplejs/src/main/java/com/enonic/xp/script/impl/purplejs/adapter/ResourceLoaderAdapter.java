package com.enonic.xp.script.impl.purplejs.adapter;

import io.purplejs.core.resource.Resource;
import io.purplejs.core.resource.ResourceLoader;
import io.purplejs.core.resource.ResourcePath;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

public final class ResourceLoaderAdapter
    implements ResourceLoader
{
    private final ApplicationKey applicationKey;

    private final ResourceService service;

    public ResourceLoaderAdapter( final ApplicationKey applicationKey, final ResourceService service )
    {
        this.applicationKey = applicationKey;
        this.service = service;
    }

    @Override
    public Resource loadOrNull( final ResourcePath path )
    {
        final ResourceKey key = ResourceKey.from( this.applicationKey, path.getPath() );
        final com.enonic.xp.resource.Resource resource = this.service.getResource( key );

        if ( !resource.exists() )
        {
            return null;
        }

        return new ResourceAdapter( resource );
    }
}
