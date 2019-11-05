package com.enonic.xp.repo.impl.search;

import java.util.List;

import com.enonic.xp.repo.impl.search.result.SearchResult;

public interface SearchDao
{

    List<SearchResult> search( final SearchRequest searchRequest );

}
