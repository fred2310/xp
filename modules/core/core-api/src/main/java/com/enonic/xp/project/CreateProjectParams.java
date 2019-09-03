package com.enonic.xp.project;


import com.enonic.xp.attachment.Attachment;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class CreateProjectParams
{
    public static final CreateProjectParams DEFAULT_PROJECT_CREATE_PARAMS =
            CreateProjectParams.create().
                    name( ProjectName.DEFAULT_PROJECT_NAME ).
                    displayName( "Default" )
                    .build();

    private final ProjectName name;
    private final String displayName;
    private final String description;
    private final Attachment icon;

    private CreateProjectParams( final Builder builder )
    {
        Preconditions.checkNotNull( builder.displayName, "displayName cannot be null" );

        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.icon = builder.icon;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final CreateProjectParams source )
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

        if (  o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final CreateProjectParams that = (CreateProjectParams) o;
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

        public Builder( CreateProjectParams project )
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

        public CreateProjectParams build()
        {
            return new CreateProjectParams( this );
        }
    }
}
