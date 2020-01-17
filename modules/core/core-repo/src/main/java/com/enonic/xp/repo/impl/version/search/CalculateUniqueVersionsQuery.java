package com.enonic.xp.repo.impl.version.search;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.node.AbstractQuery;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.SearchMode;

public class CalculateUniqueVersionsQuery
    extends AbstractQuery
{
    private final Branches branches;

    private final NodePath nodePath;

    private final ExcludeEntries excludes;

    private boolean deleted;

    private CalculateUniqueVersionsQuery( Builder builder )
    {
        super( builder );
        this.branches = builder.branches;
        this.nodePath = builder.nodePath;
        this.excludes = builder.excludes;
        this.deleted = builder.deleted;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public ExcludeEntries getExcludes()
    {
        return excludes;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted( final boolean deleted )
    {
        this.deleted = deleted;
    }

    public void setSearchMode( final SearchMode mode )
    {
        this.searchMode = mode;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public static class Builder
        extends AbstractQuery.Builder<Builder>
    {
        private Branches branches;

        private NodePath nodePath;

        private ExcludeEntries excludes = ExcludeEntries.empty();

        private boolean deleted = false;

        public Builder()
        {
            super();
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder excludes( final ExcludeEntries excludes )
        {
            this.excludes = excludes;
            return this;
        }

        public Builder deleted( final boolean deleted )
        {
            this.deleted = deleted;
            return this;
        }

        public CalculateUniqueVersionsQuery build()
        {
            return new CalculateUniqueVersionsQuery( this );
        }
    }
}

