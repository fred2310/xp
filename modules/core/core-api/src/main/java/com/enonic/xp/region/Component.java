package com.enonic.xp.region;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

@Beta
public abstract class Component
{
    private Region region = null;

    protected Component()
    {
    }

    public abstract ComponentType getType();

    public ComponentPath getPath()
    {
        return region == null ? null : ComponentPath.from( region.getRegionPath(), region.getIndex( this ) );
    }

    public Region getRegion()
    {
        return region;
    }

    void setRegion( final Region region )
    {
        this.region = region;
    }

    public abstract Component copy();

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "type", getType() ).
            add( "path", getPath() ).
            toString();
    }

    public static class Builder
    {
        protected Builder()
        {
            // Default
        }
    }
}
