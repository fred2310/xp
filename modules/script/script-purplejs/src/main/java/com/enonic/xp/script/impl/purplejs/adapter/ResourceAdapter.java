package com.enonic.xp.script.impl.purplejs.adapter;

import com.google.common.io.ByteSource;

import io.purplejs.core.resource.Resource;
import io.purplejs.core.resource.ResourcePath;

final class ResourceAdapter
    implements Resource
{
    private final com.enonic.xp.resource.Resource from;

    ResourceAdapter( final com.enonic.xp.resource.Resource from )
    {
        this.from = from;
    }

    @Override
    public ResourcePath getPath()
    {
        return ResourcePath.from( this.from.getKey().getPath() );
    }

    @Override
    public long getSize()
    {
        return this.from.getSize();
    }

    @Override
    public long getLastModified()
    {
        return this.from.getTimestamp();
    }

    @Override
    public ByteSource getBytes()
    {
        return this.from.getBytes();
    }
}
