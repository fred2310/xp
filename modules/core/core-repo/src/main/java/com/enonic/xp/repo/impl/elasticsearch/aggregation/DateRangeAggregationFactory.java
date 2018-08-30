package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import java.time.Instant;
import java.util.Collection;

import org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange;
import org.joda.time.DateTime;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.DateRangeBucket;

class DateRangeAggregationFactory
    extends AggregationsFactory
{
    static BucketAggregation create( final InternalDateRange dateRangeAggregation )
    {
        return BucketAggregation.bucketAggregation( dateRangeAggregation.getName() ).
            buckets( createBuckets( dateRangeAggregation.getBuckets() ) ).
            build();
    }

    private static Buckets createBuckets( final Collection<? extends InternalDateRange.Bucket> buckets )
    {
        final Buckets.Builder bucketsBuilder = new Buckets.Builder();

        for ( final InternalDateRange.Bucket bucket : buckets )
        {
            final DateRangeBucket.Builder builder = DateRangeBucket.create().
                key( bucket.getKey() ).
                docCount( bucket.getDocCount() ).
                from( toInstant( bucket.getFrom() ) ).
                to( toInstant( bucket.getTo() ) );

            doAddSubAggregations( bucket, builder );

            bucketsBuilder.add( builder.build() );
        }

        return bucketsBuilder.build();
    }

    private static Instant toInstant( final Object dateTime )
    {
        final DateTime dt = (DateTime) dateTime;
        if ( dt == null )
        {
            return null;
        }
        return java.time.Instant.ofEpochMilli( dt.getMillis() );
    }
}
