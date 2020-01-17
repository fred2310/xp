package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.CalculateUniqueVersionsQueryFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.StoreQueryFieldNameResolver;
import com.enonic.xp.repo.impl.version.search.CalculateUniqueVersionsQuery;

class NodeUniqueVersionsTranslator
    implements QueryTypeTranslator
{
    private final CalculateUniqueVersionsQuery query;

    private final QueryFieldNameResolver fieldNameResolver = new StoreQueryFieldNameResolver();

    NodeUniqueVersionsTranslator( final CalculateUniqueVersionsQuery query )
    {
        this.query = query;
    }

    @Override
    public QueryFieldNameResolver getFieldNameResolver()
    {
        return this.fieldNameResolver;
    }

    @Override
    public int getBatchSize()
    {
        return query.getSize();
    }

    @Override
    public SearchOptimizer getSearchOptimizer()
    {
        return query.getSearchOptimizer();
    }

    @Override
    public QueryBuilder createQueryBuilder( final Filters additionalFilters )
    {
        return CalculateUniqueVersionsQueryFactory.create().
            query( query ).
            build().
            execute();
    }

}
