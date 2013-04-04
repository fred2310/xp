package com.enonic.wem.core.index.elastic.searchsource;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.core.index.content.ContentIndexField;

public class SearchSourceFactory
{
    public static SearchSourceBuilder create( final ContentIndexQuery contentIndexQuery )
    {
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        final String fullTextQuery = contentIndexQuery.getFullTextSearchString();

        if ( Strings.isNullOrEmpty( fullTextQuery ) )
        {
            searchSourceBuilder.query( QueryBuilders.matchAllQuery() );
        }
        else
        {
            final QueryStringQueryBuilder fulltextQuery = QueryBuilders.queryString( fullTextQuery );
            fulltextQuery.lenient( true );
            searchSourceBuilder.query( fulltextQuery );
        }

        buildFilters( contentIndexQuery, searchSourceBuilder );

        if ( contentIndexQuery.isIncludeFacets() )
        {
            final String facetsDefinition = contentIndexQuery.getFacets();

            if ( !Strings.isNullOrEmpty( facetsDefinition ) )
            {
                searchSourceBuilder.facets( facetsDefinition.getBytes() );
            }
        }

        return searchSourceBuilder;
    }

    private static void buildFilters( final ContentIndexQuery contentIndexQuery, final SearchSourceBuilder searchSourceBuilder )
    {
        Set<FilterBuilder> filtersToApply = Sets.newHashSet();

        createAndAddContentTypeFilterIfApplicable( contentIndexQuery, filtersToApply );
        createAndAddSpacesFilterIfApplicable( contentIndexQuery, filtersToApply );

        if ( filtersToApply.isEmpty() )
        {
            return;
        }

        if ( filtersToApply.size() == 1 )
        {
            searchSourceBuilder.filter( filtersToApply.iterator().next() );
            return;
        }

        wrapAllFiltersInBooleanFilterAndAdd( searchSourceBuilder, filtersToApply );
    }

    private static void wrapAllFiltersInBooleanFilterAndAdd( final SearchSourceBuilder searchSourceBuilder,
                                                             final Set<FilterBuilder> filtersToApply )
    {
        BoolFilterBuilder boolFilterBuilder = new BoolFilterBuilder();

        final Iterator<FilterBuilder> filterIterator = filtersToApply.iterator();

        while ( filterIterator.hasNext() )
        {
            boolFilterBuilder.must( filterIterator.next() );
        }

        searchSourceBuilder.filter( boolFilterBuilder );
    }

    private static void createAndAddSpacesFilterIfApplicable( final ContentIndexQuery contentIndexQuery,
                                                              final Set<FilterBuilder> filtersToApply )
    {
        if ( contentIndexQuery.getSpaceNames() != null && contentIndexQuery.getSpaceNames().isNotEmpty() )
        {
            Collection<String> spaceNames = getCollectionAsLowercase( contentIndexQuery.getSpaceNames().getAsStringSet() );
            filtersToApply.add( createFilter( spaceNames, ContentIndexField.SPACE ) );
        }
    }

    private static void createAndAddContentTypeFilterIfApplicable( final ContentIndexQuery contentIndexQuery,
                                                                   final Set<FilterBuilder> filtersToApply )
    {
        if ( contentIndexQuery.getContentTypeNames() != null && contentIndexQuery.getContentTypeNames().isNotEmpty() )
        {
            Collection<String> contentTypeNames = getCollectionAsLowercase( contentIndexQuery.getContentTypeNames().getAsStringSet() );
            final FilterBuilder contentTypeFilter = createFilter( contentTypeNames, ContentIndexField.CONTENT_TYPE );
            filtersToApply.add( contentTypeFilter );
        }
    }

    private static FilterBuilder createFilter( final Collection<String> filterElementNames, final String fieldName )
    {
        return new TermsFilterBuilder( fieldName, filterElementNames );
    }


    private static Collection<String> getCollectionAsLowercase( final Collection<String> collection )
    {
        Collection<String> lowerCaseStrings = Collections2.transform( collection, new Function<String, String>()
        {
            public String apply( String str )
            {
                return str.toLowerCase();
            }
        } );

        return lowerCaseStrings;
    }

}
