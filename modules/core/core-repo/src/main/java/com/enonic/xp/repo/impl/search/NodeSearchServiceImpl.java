package com.enonic.xp.repo.impl.search;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Sets;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionsQuery;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchSource;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.commit.storage.CommitIndexPath;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

@Component
public class NodeSearchServiceImpl
    implements NodeSearchService
{
    private static final ReturnFields VERSION_RETURN_FIELDS =
        ReturnFields.from( VersionIndexPath.VERSION_ID, VersionIndexPath.NODE_BLOB_KEY, VersionIndexPath.INDEX_CONFIG_BLOB_KEY,
                           VersionIndexPath.ACCESS_CONTROL_BLOB_KEY, VersionIndexPath.BINARY_BLOB_KEYS, VersionIndexPath.TIMESTAMP,
                           VersionIndexPath.NODE_PATH, VersionIndexPath.NODE_ID, VersionIndexPath.COMMIT_ID );

    private static final ReturnFields BRANCH_RETURN_FIELDS =
        ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID, BranchIndexPath.NODE_BLOB_KEY,
                           BranchIndexPath.INDEX_CONFIG_BLOB_KEY, BranchIndexPath.ACCESS_CONTROL_BLOB_KEY, BranchIndexPath.STATE,
                           BranchIndexPath.PATH, BranchIndexPath.TIMESTAMP );

    private static final ReturnFields COMMIT_RETURN_FIELDS =
        ReturnFields.from( CommitIndexPath.COMMIT_ID, CommitIndexPath.MESSAGE, CommitIndexPath.COMMITTER, CommitIndexPath.TIMESTAMP );

    private SearchDao searchDao;

    @Override
    public SearchResult query( final NodeQuery query, final SearchSource source )
    {
        return doQuery( query, ReturnFields.empty(), source );
    }

    @Override
    public SearchResult query( final NodeQuery query, ReturnFields returnFields, final SearchSource source )
    {
        return doQuery( query, returnFields, source );
    }

    private SearchResult doQuery( final NodeQuery query, final ReturnFields returnFields, final SearchSource source )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( source ).
            query( query ).
            returnFields( returnFields ).
            build();

        return searchDao.search( searchRequest ).get( 0 );
    }

    @Override
    public SearchResult query( final NodeBranchQuery nodeBranchQuery, final SearchSource source )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( source ).
            returnFields( BRANCH_RETURN_FIELDS ).
            query( nodeBranchQuery ).
            build();

        return searchDao.search( searchRequest ).get( 0 );
    }

    @Override
    public SearchResult query( final NodeVersionQuery query, final SearchSource source )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( source ).
            returnFields( VERSION_RETURN_FIELDS ).
            query( query ).
            build();

        return searchDao.search( searchRequest ).get( 0 );
    }

    @Override
    public SearchResult query( final NodeCommitQuery query, final SearchSource source )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( source ).
            returnFields( COMMIT_RETURN_FIELDS ).
            query( query ).
            build();

        return searchDao.search( searchRequest ).get( 0 );
    }

    @Override
    public SearchResult query( final NodeVersionDiffQuery query, final SearchSource source )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( source ).
            returnFields( ReturnFields.from( BranchIndexPath.VERSION_ID ) ).
            query( query ).
            build();

        final List<SearchResult> results = searchDao.search( searchRequest );

        Set set1 = results.get( 0 ).
            getHits().
            stream().
            map( hit -> hit.getField( "versionid" ).getSingleValue() ).
            collect( Collectors.toSet() );

        Set set2 = results.get( 1 ).
            getHits().
            stream().
            map( hit -> hit.getField( "versionid" ).getSingleValue() ).
            collect( Collectors.toSet() );

        Set set3 = results.get( 2 ).
            getHits().
            stream().
            map( hit -> hit.getField( "versionid" ).getSingleValue() ).
            collect( Collectors.toSet() );

        Set set4 = results.get( 3 ).
            getHits().
            stream().
            map( hit -> (String) hit.getField( "versionid" ).getSingleValue() ).
            collect( Collectors.toSet() );

        final Sets.SetView<String> view1 = Sets.symmetricDifference( set1, set2 );
        final Sets.SetView view2 = Sets.symmetricDifference( set3, set4 );

        // desired version ids
        final Set<String> result = Sets.union( view1, view2 ).immutableCopy();

        if ( result.size() > 0 )
        {
            return searchDao.search( SearchRequest.create().
                searchSource(
                    SingleRepoStorageSource.create( ContextAccessor.current().getRepositoryId(), SingleRepoStorageSource.Type.VERSION ) ).
                returnFields( VERSION_RETURN_FIELDS ).
                query( NodeVersionsQuery.create().
                    addIds( result ).
                    size( -1 ).
                    batchSize( 20000 ).
                    build() ).
                build() ).get( 0 );
        }
        else
        {
            return SearchResult.create().hits( SearchHits.create().build() ).build();
        }

       /* final NodeVersionQuery version = NodeVersionQuery.create().
            size( -1 ).
            nodeId( params.getNodeId() ).
            addOrderBy( FieldOrderExpr.create( VersionIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) ).
            build();

        return FindNodeVersionsCommand.create().
            query( query ).
            searchService( this.nodeSearchService ).
            build().
            execute();*/

    }

    @Reference
    public void setSearchDao( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }
}
