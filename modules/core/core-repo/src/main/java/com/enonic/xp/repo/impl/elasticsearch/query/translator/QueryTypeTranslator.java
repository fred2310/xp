package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

interface QueryTypeTranslator
{
    List<QueryBuilder> createQueryBuilder( final Filters additionalFilters );

    QueryFieldNameResolver getFieldNameResolver();

    int getBatchSize();

    SearchOptimizer getSearchOptimizer();

}
