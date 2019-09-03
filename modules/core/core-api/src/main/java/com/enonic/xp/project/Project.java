package com.enonic.xp.project;

import com.enonic.xp.attachment.Attachment;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public final class Project
{
    private final ProjectName name;
    private final String displayName;
    private final String description;
    private final Attachment icon;

    public Project( final Builder builder )
    {
        Preconditions.checkNotNull( builder.displayName, "displayName is mandatory for a Project" );

        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.icon = builder.icon;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Project source )
    {
        return new Builder( source );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof Project ) )
        {
            return false;
        }
        final Project that = (Project) o;
        return Objects.equal( name, that.name ) &&
                Objects.equal( displayName, that.displayName ) &&
                Objects.equal( description, that.description ) &&
                Objects.equal( icon, that.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, displayName, description, icon );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this )
                .add( "name", name )
                .add( "displayName", displayName )
                .add( "description", description )
                .add( "icon", icon )
                .toString();
    }

    public static class Builder
    {
        private ProjectName name;
        private String displayName;
        private String description;
        private Attachment icon;

        private Builder()
        {

        }

        public Builder( Project project )
        {
            this.name = project.name;
            this.displayName = project.displayName;
            this.description = project.description;
            this.icon = project.icon;
        }

        public Builder name( ProjectName name )
        {
            this.name = name;
            return this;
        }

        public Builder displayName( String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder description( String description )
        {
            this.description = description;
            return this;
        }

        public Builder icon( Attachment icon )
        {
            this.icon = icon;
            return this;
        }

        public Project build()
        {
            return new Project( this );
        }
    }
}
