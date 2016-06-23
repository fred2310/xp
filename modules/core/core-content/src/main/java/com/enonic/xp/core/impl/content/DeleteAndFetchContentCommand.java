package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;


final class DeleteAndFetchContentCommand
    extends AbstractContentCommand
{
    private final DeleteContentParams params;

    private DeleteAndFetchContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    ContentIds execute()
    {
        params.validate();

        try
        {
            final ContentIds deletedContents = doExecute();
            nodeService.refresh( RefreshMode.SEARCH );
            return deletedContents;
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private ContentIds doExecute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( this.params.getContentPath() );
        final Node nodeToDelete = this.nodeService.getByPath( nodePath );

        final NodeIds.Builder nodesToDelete = NodeIds.create();
        recursiveDelete( nodeToDelete.id(), nodesToDelete );

        this.nodeService.refresh( RefreshMode.ALL );

        return ContentIds.from( nodesToDelete.build().getAsStrings() );
    }

    private void recursiveDelete( NodeId nodeToDelete, NodeIds.Builder deletedNodes )
    {
        final FindNodesByParentResult findNodesByParentResult = this.nodeService.findByParent( FindNodesByParentParams.create().
            parentId( nodeToDelete ).
            build() );

        for ( NodeId childNodeToDelete : findNodesByParentResult.getNodeIds() )
        {
            recursiveDelete( childNodeToDelete, deletedNodes );
        }

        final CompareStatus status = getCompareStatus( nodeToDelete );

        if ( status == CompareStatus.NEW )
        {
            final NodeIds deletedNodeIds = nodeService.deleteById( nodeToDelete );
            deletedNodes.addAll( deletedNodeIds );
            return;
        }

        if ( this.params.isDeleteOnline() )
        {
            final Context currentContext = ContextAccessor.current();
            deleteNodeInContext( nodeToDelete, currentContext );
            final NodeIds deletedNodeIds = deleteNodeInContext( nodeToDelete, ContextBuilder.from( currentContext ).
                branch( ContentConstants.BRANCH_MASTER ).
                build() );
            deletedNodes.addAll( deletedNodeIds );
            return;
        }

        final SetNodeStateResult setNodeStateResult = this.nodeService.setNodeState( SetNodeStateParams.create().
            nodeId( nodeToDelete ).
            nodeState( NodeState.PENDING_DELETE ).
            build() );

        //  deletedNodes.addAll( setNodeStateResult.getUpdatedNodes() );
    }

    private CompareStatus getCompareStatus( final NodeId nodeToDelete )
    {
        final Context context = ContextAccessor.current();
        final Branch currentBranch = context.getBranch();

        final NodeComparison compare;
        if ( currentBranch.equals( ContentConstants.BRANCH_DRAFT ) )
        {
            compare = this.nodeService.compare( nodeToDelete, ContentConstants.BRANCH_MASTER );
        }
        else
        {
            compare = this.nodeService.compare( nodeToDelete, ContentConstants.BRANCH_DRAFT );
        }
        return compare.getCompareStatus();
    }

    private NodeIds deleteNodeInContext( final NodeId nodeToDelete, final Context context )
    {
        return context.callWith( () -> this.nodeService.deleteById( nodeToDelete ) );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private DeleteContentParams params;

        public Builder params( final DeleteContentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            Preconditions.checkNotNull( params );
        }

        public DeleteAndFetchContentCommand build()
        {
            validate();
            return new DeleteAndFetchContentCommand( this );
        }
    }

}
