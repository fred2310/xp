package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.version.search.CalculateUniqueVersionsQuery;
import com.enonic.xp.repo.impl.version.search.ExcludeEntries;
import com.enonic.xp.repo.impl.version.search.ExcludeEntry;

public class CalculateUniqueVersionsQueryFactory
{
    private final Branches branches;

    private final NodePath nodePath;

    private final ExcludeEntries excludes;

    private final boolean deleted;

    private CalculateUniqueVersionsQueryFactory( Builder builder )
    {
        this.branches = builder.query.getBranches();
        this.nodePath = builder.query.getNodePath();
        this.excludes = builder.query.getExcludes();
        this.deleted = builder.query.isDeleted();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public QueryBuilder execute()
    {
        return createDiffQuery();
    }

    private QueryBuilder createDiffQuery()
    {
        final BoolQueryBuilder inSourceOnly = onlyInQuery( this.branches );

        final BoolQueryBuilder deletedInSourceOnly = deletedOnlyQuery( this.branches );

        return deleted ? wrapInPathQueryIfNecessary( deletedInSourceOnly ) : wrapInPathQueryIfNecessary( inSourceOnly );
    }

    private BoolQueryBuilder onlyInQuery( final Branches branches )
    {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        branches.forEach( branch -> boolQueryBuilder.should( isInBranch( branch ) ) );

        return boolQueryBuilder;
    }

    private BoolQueryBuilder deletedOnlyQuery( final Branches branches )
    {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        branches.forEach( branch -> boolQueryBuilder.should( deletedInBranch( branch ) ) );

        return boolQueryBuilder;
    }

    private BoolQueryBuilder deletedInBranch( final Branch sourceBranch )
    {
        return new BoolQueryBuilder().
            must( isDeleted() ).
            must( new TermQueryBuilder( BranchIndexPath.BRANCH_NAME.toString(), sourceBranch.getValue() ) );
    }

    private TermQueryBuilder isDeleted()
    {
        return new TermQueryBuilder( BranchIndexPath.STATE.toString(), NodeState.PENDING_DELETE.value() );
    }

    private BoolQueryBuilder wrapInPathQueryIfNecessary( final BoolQueryBuilder sourceTargetCompares )
    {

        final BoolQueryBuilder pathFilter = new BoolQueryBuilder();

        boolean addedPathFilter = false;

        if ( this.nodePath != null && !this.nodePath.isRoot() )
        {
            addedPathFilter = true;
            pathFilter.
                must( hasPath( this.nodePath, true ) );
        }

        if ( !this.excludes.isEmpty() )
        {
            addedPathFilter = true;
            for ( final ExcludeEntry exclude : excludes )
            {
                pathFilter.
                    mustNot( hasPath( exclude.getNodePath(), exclude.isRecursive() ) );
            }
        }

        return addedPathFilter
            ? pathFilter.filter( sourceTargetCompares )
            : QueryBuilders.boolQuery().filter( QueryBuilders.matchAllQuery() ).filter( sourceTargetCompares );
    }

    private QueryBuilder hasPath( final NodePath nodePath, final boolean recursive )
    {
        final String queryPath = nodePath.toString().toLowerCase();

        final BoolQueryBuilder pathQuery = new BoolQueryBuilder().
            should( new TermQueryBuilder( BranchIndexPath.BRANCH_NAME.getPath(), queryPath ) );

        if ( recursive )
        {
            pathQuery.should( new WildcardQueryBuilder( BranchIndexPath.PATH.getPath(),
                                                        queryPath.endsWith( "/" ) ? queryPath + "*" : queryPath + "/*" ) );
        }

        return pathQuery;
    }

    private TermQueryBuilder isInBranch( final Branch source )
    {
        return new TermQueryBuilder( BranchIndexPath.BRANCH_NAME.toString(), source.toString() );
    }

    public static final class Builder
    {
        private CalculateUniqueVersionsQuery query;

        private Builder()
        {
        }

        public Builder query( CalculateUniqueVersionsQuery query )
        {
            this.query = query;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.query );
        }

        public CalculateUniqueVersionsQueryFactory build()
        {
            this.validate();
            return new CalculateUniqueVersionsQueryFactory( this );
        }
    }
}
