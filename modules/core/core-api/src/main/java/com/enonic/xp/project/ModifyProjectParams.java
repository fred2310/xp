package com.enonic.xp.project;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.CreateAttachment;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class ModifyProjectParams
{
    private final ProjectName name;
    private final String displayName;
    private final String description;
    private final CreateAttachment icon;

    private ModifyProjectParams( final Builder builder )
    {
        Preconditions.checkNotNull( builder.displayName, "displayName cannot be null" );

        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.icon = builder.icon;
    }

    public ProjectName getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public CreateAttachment getIcon()
    {
        return icon;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final ModifyProjectParams source )
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

        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ModifyProjectParams that = (ModifyProjectParams) o;
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
        private CreateAttachment icon;

        private Builder()
        {

        }

        public Builder( ModifyProjectParams project )
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

        public Builder icon( CreateAttachment icon )
        {
            this.icon = icon;
            return this;
        }

        public ModifyProjectParams build()
        {
            return new ModifyProjectParams( this );
        }
    }

}
