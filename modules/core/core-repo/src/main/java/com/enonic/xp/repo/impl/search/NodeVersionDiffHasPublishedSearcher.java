package com.enonic.xp.repo.impl.search;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.aggregation.CardinalityAggregation;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.CardinalityAggregationQuery;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchSource;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.search.CalculateUniqueVersionsQuery;

@Component
public class NodeVersionDiffHasPublishedSearcher
{

    private SearchDao searchDao;

    public NodeVersionDiffHasPublishedSearcher( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }

    public int find( final CalculateUniqueVersionsQuery query, final SearchSource source )
    {
        return calculateUniqueVersions( query, source );
    }

    private int calculateUniqueVersions( final CalculateUniqueVersionsQuery query, final SearchSource source )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            searchSource( source ).
            returnFields( ReturnFields.from( BranchIndexPath.VERSION_ID ) ).
            query( query ).
            build();

        query.setAggregations( AggregationQueries.create().
            add( CardinalityAggregationQuery.create( "count" ).
                fieldName( BranchIndexPath.VERSION_ID.toString() ).
                build() ).
            build() );

        SearchResult result = searchDao.search( searchRequest );

        CardinalityAggregation uniqueVersions = (CardinalityAggregation) result.getAggregations().get( "count" );

        return Double.valueOf( uniqueVersions.getValue() ).intValue();
    }

    @Reference
    public void setSearchDao( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }
}
