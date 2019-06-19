package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repository.RepositoryService;

public abstract class AbstractToChildBranchCommand
    extends AbstractNodeCommand
{
    protected final Branch parentBranch;

    protected final Branches childBranches;

    protected final NodeIds ids;

    protected final RepositoryService repositoryService;

    protected AbstractToChildBranchCommand( final Builder builder )
    {
        super( builder );
        parentBranch = builder.parentBranch;
        childBranches = builder.childBranches;
        ids = builder.ids;
        repositoryService = builder.repositoryService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void execute()
    {
        //If there is no child branch, return
        final Branches childBranches = this.childBranches == null ? getChildBranches( parentBranch ) : this.childBranches;
        execute( parentBranch, childBranches, ids );
    }

    protected abstract void execute( final Branch parentBranch, final Branches childBranches, final NodeIds nodeIds );

    protected Branches getChildBranches( final Branch parentBranch )
    {
        return NodeHelper.runAsAdmin( () -> repositoryService.get( ContextAccessor.current().getRepositoryId() ).
            getChildBranches( parentBranch ) );
    }

    protected void refreshAll()
    {
        RefreshCommand.create().
            refreshMode( RefreshMode.ALL ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

    protected void runInBranch( final Branch branch, final Runnable runnable )
    {
        ContextBuilder.from( ContextAccessor.current() ).
            branch( branch ).
            build().
            runWith( runnable );
    }

    protected NodeComparisons getNodeComparisons( final NodeBranchEntries nodeBranchEntries, final Branch target )
    {
        return CompareNodesCommand.create().
            nodeIds( NodeIds.from( nodeBranchEntries.getKeys() ) ).
            storageService( this.nodeStorageService ).
            target( target ).
            build().
            execute();
    }

    protected NodeBranchEntries getNodeBranchEntries( final NodeIds nodeIds )
    {
        return FindNodeBranchEntriesByIdCommand.create( this ).
            ids( nodeIds ).
            build().
            execute();
    }


    public static class Builder<T extends Builder>
        extends AbstractNodeCommand.Builder<T>
    {
        private Branch parentBranch;

        private Branches childBranches;

        private NodeIds ids;

        private RepositoryService repositoryService;

        protected Builder()
        {
            super();
        }

        public T parentBranch( final Branch parentBranch )
        {
            this.parentBranch = parentBranch;
            return (T) this;
        }

        public T childBranches( final Branches childBranches )
        {
            this.childBranches = childBranches;
            return (T) this;
        }

        public T ids( final NodeIds ids )
        {
            this.ids = ids;
            return (T) this;
        }

        public T repositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return (T) this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( parentBranch );
            Preconditions.checkNotNull( ids );
            Preconditions.checkNotNull( repositoryService );
        }
    }
}
