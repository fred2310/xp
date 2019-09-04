package com.enonic.xp.highlight;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class HighlightedFields
    implements Iterable<HighlightedField>
{
    private final ImmutableMap<String, HighlightedField> highlightedFields;

    private HighlightedFields( final Builder builder )
    {
        this.highlightedFields = ImmutableMap.copyOf( builder.highlightedFields );
    }

    public int size()
    {
        return highlightedFields.size();
    }

    public boolean isEmpty()
    {
        return highlightedFields.isEmpty();
    }

    public HighlightedField get( final String name )
    {
        return highlightedFields.get( name );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final HighlightedFields that = (HighlightedFields) o;
        return Objects.equals( highlightedFields, that.highlightedFields );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( highlightedFields );
    }

    public static HighlightedFields empty()
    {
        return HighlightedFields.create().build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( HighlightedFields source )
    {
        return new Builder( source );
    }

    @Override
    public Iterator<HighlightedField> iterator()
    {
        return highlightedFields.values().iterator();
    }

    public static final class Builder
    {
        final Map<String, HighlightedField> highlightedFields = Maps.newHashMap();

        private Builder()
        {
        }

        private Builder( final HighlightedFields source )
        {
            if ( source == null )
            {
                return;
            }
            highlightedFields.putAll( source.highlightedFields );
        }

        public Builder add( final HighlightedField highlightedField )
        {
            highlightedFields.put( highlightedField.getName(), highlightedField );
            return this;
        }

        public HighlightedFields build()
        {
            return new HighlightedFields( this );
        }
    }

}