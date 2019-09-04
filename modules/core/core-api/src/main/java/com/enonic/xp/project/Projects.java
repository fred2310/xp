package com.enonic.xp.project;

import com.enonic.xp.support.AbstractImmutableEntitySet;
import com.google.common.collect.ImmutableSet;

public class Projects extends AbstractImmutableEntitySet<Project>
{
    public Projects( ImmutableSet<Project> set )
    {
        super( set );
    }

    public static Projects from( final Project... contentLayers )
    {
        return new Projects( ImmutableSet.copyOf( contentLayers ) );
    }

    public static Projects from( final Iterable<Project> contentLayers )
    {
        return new Projects( ImmutableSet.copyOf( contentLayers ) );
    }
}
