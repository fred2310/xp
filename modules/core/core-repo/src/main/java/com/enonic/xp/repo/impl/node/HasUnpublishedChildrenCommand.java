package com.enonic.xp.repo.impl.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.version.search.CalculateUniqueVersionsQuery;
import com.enonic.xp.repo.impl.version.search.ExcludeEntries;
import com.enonic.xp.repo.impl.version.search.ExcludeEntry;

public class HasUnpublishedChildrenCommand
    extends AbstractNodeCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( HasUnpublishedChildrenCommand.class );

    private final Branch target;

    private final NodeId parent;

    private HasUnpublishedChildrenCommand( final Builder builder )
    {
        super( builder );
        target = builder.target;
        parent = builder.parent;
    }

    public boolean execute()
    {
        final Node parentNode = doGetById( parent );

        if ( parentNode == null )
        {
            return false;
        }

        final CalculateUniqueVersionsQuery.Builder queryBuilder = CalculateUniqueVersionsQuery.create().
            nodePath( parentNode.path() ).
            excludes( ExcludeEntries.create().
                add( new ExcludeEntry( parentNode.path(), false ) ).
                build() ).
            size( 0 );

        final SingleRepoStorageSource storageSource =
            SingleRepoStorageSource.create( ContextAccessor.current().getRepositoryId(), SingleRepoStorageSource.Type.BRANCH );

        final Branch source = ContextAccessor.current().getBranch();

        final int uniqueSource = this.nodeSearchService.query( queryBuilder.branches( Branches.from( source ) ).build(), storageSource );
        final int uniqueTarget = this.nodeSearchService.query( queryBuilder.branches( Branches.from( target ) ).build(), storageSource );
        final int uniqueBoth =
            this.nodeSearchService.query( queryBuilder.branches( Branches.from( source, target ) ).build(), storageSource );

        final int uniqueSourceDeleted =
            this.nodeSearchService.query( queryBuilder.branches( Branches.from( source ) ).deleted( true ).build(), storageSource );
        final int uniqueTargetDeleted =
            this.nodeSearchService.query( queryBuilder.branches( Branches.from( target ) ).deleted( true ).build(), storageSource );
        final int uniqueBothDeleted =
            this.nodeSearchService.query( queryBuilder.branches( Branches.from( source, target ) ).deleted( true ).build(), storageSource );

        return uniqueSource != uniqueBoth || uniqueTarget != uniqueBoth || uniqueSourceDeleted != uniqueBothDeleted ||
            uniqueTargetDeleted != uniqueBothDeleted;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Branch target;

        private NodeId parent;

        private Builder()
        {
        }

        public Builder target( final Branch val )
        {
            target = val;
            return this;
        }

        public Builder parent( final NodeId val )
        {
            parent = val;
            return this;
        }

        public HasUnpublishedChildrenCommand build()
        {
            return new HasUnpublishedChildrenCommand( this );
        }
    }
}
