package com.enonic.xp.region;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class Regions
    extends AbstractImmutableEntityList<Region>
{
    private Regions( final ImmutableList<Region> list )
    {
        super( list );
    }

    public static Regions empty()
    {
        final ImmutableList<Region> list = ImmutableList.of();
        return new Regions( list );
    }

    public static Regions from( final Region... regions )
    {
        return regions != null ? new Regions( ImmutableList.copyOf( regions ) ) : Regions.empty();
    }

    public static Regions from( final Iterable<? extends Region> descriptors )
    {
        return descriptors != null ? new Regions( ImmutableList.copyOf( descriptors ) ) : Regions.empty();
    }

    public static Regions from( final Collection<? extends Region> descriptors )
    {
        return descriptors != null ? new Regions( ImmutableList.copyOf( descriptors ) ) : Regions.empty();
    }
}
