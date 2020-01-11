package com.enonic.xp.repo;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.repo.impl.version.TestQueryType;

public class ResolveSyncWorkPerformanceTest
{
    @Test
    public void testReferencePerformance()
        throws Exception
    {
        Options opt = new OptionsBuilder().include( this.getClass().getName() + ".*" ).mode( Mode.AverageTime ).timeUnit(
            TimeUnit.SECONDS ).warmupTime( TimeValue.seconds( 1 ) ).warmupIterations( 10 ).measurementTime(
            TimeValue.seconds( 1 ) ).measurementIterations( 10 ).threads( 1 ).forks( 1 ).shouldFailOnError( true ).build();

        new Runner( opt ).run();
    }

    @Benchmark
    public void inMemory( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.IN_MEMORY, state.NON_PUBLISHED_NODES_ROOT, state.NODE_SIZE + 1 ) ) );
    }

    @Benchmark
    public void inMemory_half( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.IN_MEMORY, state.HALF_PUBLISHED_NODES_ROOT, state.NODE_SIZE / 2 ) ) );
    }

    @Benchmark
    public void inMemory_all( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.IN_MEMORY, state.PUBLISHED_NODES_ROOT, 0 ) ) );
    }

    @Benchmark
    public void composite( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.COMPOSITE, state.NON_PUBLISHED_NODES_ROOT, state.NODE_SIZE + 1 ) ) );
    }

    @Benchmark
    public void composite_half( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.COMPOSITE, state.HALF_PUBLISHED_NODES_ROOT, state.NODE_SIZE / 2 ) ) );
    }

    @Benchmark
    public void composite_all( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.COMPOSITE, state.PUBLISHED_NODES_ROOT, 0 ) ) );
    }

    @Benchmark
    public void rare( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.RARE, state.NON_PUBLISHED_NODES_ROOT, state.NODE_SIZE + 1 ) ) );
    }

    @Benchmark
    public void rare_half( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.RARE, state.HALF_PUBLISHED_NODES_ROOT, state.NODE_SIZE / 2 ) ) );
    }

    @Benchmark
    public void rare_all( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.RARE, state.PUBLISHED_NODES_ROOT, 0 ) ) );
    }

    public void sortedTerms_none( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.SORTED_TERMS, state.NON_PUBLISHED_NODES_ROOT, state.NODE_SIZE + 1 ) ) );
    }

    @Benchmark
    public void sortedTerms_half( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.SORTED_TERMS, state.HALF_PUBLISHED_NODES_ROOT, state.NODE_SIZE / 2 ) ) );
    }

    @Benchmark
    public void sortedTerms_all( BenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.SORTED_TERMS, state.PUBLISHED_NODES_ROOT, 0 ) ) );
    }

    @Benchmark
    @Group("dynamic_publishing")
    public void inMemory_dynamic( DynamicBenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.IN_MEMORY, state.PUBLISHED_DYNAMIC_ROOT, state.NODE_SIZE - state.publishCount ) ) );
    }

    @Benchmark
    @Group("dynamic_publishing")
    public void composite_dynamic( DynamicBenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.COMPOSITE, state.PUBLISHED_DYNAMIC_ROOT, state.NODE_SIZE - state.publishCount ) ) );
    }

    @Benchmark
    @Group("dynamic_publishing")
    public void rare_dynamic( DynamicBenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.RARE, state.PUBLISHED_DYNAMIC_ROOT, state.NODE_SIZE - state.publishCount ) ) );
    }

    int run( DynamicBenchmarkState state, TestQueryType type, final Node root, final int expectedCount )
    {

        ResolveSyncWorkResult resolvedNodes = state.prepareResolveSyncWorkCommandBuilder().testQueryType( type ).
            nodeId( root.id() ).
            build().
            execute();

        System.out.println( "--------------expected: " + expectedCount + "--------------real: " + resolvedNodes.getSize() );

        Assertions.assertEquals( expectedCount, resolvedNodes.getSize() );

        return resolvedNodes.getSize();
    }

    int run( BenchmarkState state, TestQueryType type, final Node root, final int expectedCount )
    {

        ResolveSyncWorkResult resolvedNodes = state.prepareResolveSyncWorkCommandBuilder().testQueryType( type ).
            nodeId( root.id() ).
            build().
            execute();
        Assertions.assertEquals( expectedCount, resolvedNodes.getSize() );

        return resolvedNodes.getSize();
    }

    public static class BaseBenchmarkState
        extends ResolveSyncWorkPerformanceBootstrap
    {
        public void setup()
        {
            startClient();
            setupServices();

            CONTEXT_DRAFT.callWith( () -> {

                this.ROOT_NODE = nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode" ).build() );
                this.NON_PUBLISHED_NODES_ROOT =
                    nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode/nonPublishedRoot" ).build() );
                this.HALF_PUBLISHED_NODES_ROOT =
                    nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode/halfPublishedRoot" ).build() );
                this.PUBLISHED_NODES_ROOT = nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode/publishedRoot" ).build() );
                this.PUBLISHED_DYNAMIC_ROOT =
                    nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode/publishedDynamicRoot" ).build() );

                return 1;
            } );

        }

        public void teardown()
            throws Exception
        {
            stopClient();
        }
    }

/*    @Benchmark
    @Group("dynamic_publishing")
    public void sortedTerms_dynamic( DynamicBenchmarkState state, Blackhole bh )
    {
        bh.consume( ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith(
            () -> run( state, TestQueryType.SORTED_TERMS, state.PUBLISHED_DYNAMIC_ROOT, state.NODE_SIZE - state.publishCount ) ) );
    }*/

/*    @Benchmark
    public void branches( BenchmarkState state, Blackhole bh )
    {
        bh.consume(
            ResolveSyncWorkPerformanceBootstrap.CONTEXT_DRAFT.callWith( () -> run( state, TestQueryType.BRANCHES_IN_VERSIONS ) ) );
    }*/

    @State(Scope.Benchmark)
    public static class BenchmarkState
        extends BaseBenchmarkState
    {
        @Setup
        public void setup()
        {
            super.setup();
        }

        @TearDown
        public void teardown()
            throws Exception
        {
            super.teardown();
        }
    }

    @State(Scope.Group)
    public static class DynamicBenchmarkState
        extends BaseBenchmarkState
    {
        int publishCount = 0;

        @Setup
        public void setup()
        {
            super.setup();

            CONTEXT_DRAFT.callWith( () -> {

                this.unpublish( PUBLISHED_DYNAMIC_ROOT, true );
                publish( 0, PUBLISHED_DYNAMIC_ROOT );

                return 1;
            } );

        }

        @Setup(Level.Iteration)
        public void beforeEach()
        {
            this.publish( ++this.publishCount, this.PUBLISHED_DYNAMIC_ROOT );
        }

        @TearDown
        public void teardown()
            throws Exception
        {
            super.teardown();
        }
    }
}
