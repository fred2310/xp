package com.enonic.xp.dump;

public class RepositoryDataLoadResult
    extends AbstractLoadResult
{
    private RepositoryDataLoadResult( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractLoadResult.Builder<RepositoryDataLoadResult, Builder>
    {
        private Builder()
        {
            super();
        }

        public RepositoryDataLoadResult build()
        {
            super.build();
            return new RepositoryDataLoadResult( this );
        }
    }

}
