package com.enonic.xp.repo.impl.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;

public class ConcurrentNodeServiceImplTest
    extends AbstractNodeTest
{
    private final NodeId nodeId = NodeId.from( "testnodeid" );

    private final NodePath nodePath = NodePath.create( NodePath.ROOT, "testpath" ).build();

    private CountDownLatch startSignal;

    private CountDownLatch doneSignal;

    private NodeServiceImpl nodeService;

    @BeforeEach
    public void setUp()
        throws Exception
    {

        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexServiceInternal( indexServiceInternal );
        this.nodeService.setNodeStorageService( this.storageService );
        this.nodeService.setNodeSearchService( this.searchService );
        this.nodeService.setRepositoryService( this.repositoryService );
        this.nodeService.setBinaryService( this.binaryService );
        this.nodeService.setEventPublisher( Mockito.mock( EventPublisher.class ) );

        this.createDefaultRootNode();

        final PropertyTree data = new PropertyTree();

        this.nodeService.create( CreateNodeParams.create().
            setNodeId( nodeId ).
            name( "testpath" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        refresh();
    }

    @Test
//    @Disabled
    public void testMultiThreading_update()
        throws Exception
    {
        int reader = 1000;
        int writer = 100;

        this.startSignal = new CountDownLatch( 1 );
        this.doneSignal = new CountDownLatch( reader + writer );

        final List<Exception> errors = Collections.synchronizedList( new ArrayList<>() );

        for ( int i = 0; i < writer; i++ )
        {
            runWriter( errors );
        }

        for ( int i = 0; i < reader; i++ )
        {
            runReader( errors );
        }

        startSignal.countDown();
        doneSignal.await();

        if ( !errors.isEmpty() )
        {
            throw errors.get( 0 );
        }
    }

    private void runWriter( final List<Exception> errors )
    {
        Thread thread = new Thread( () -> {
            try
            {
                startSignal.await();

                if ( !errors.isEmpty() )
                {
                    return;
                }

                ContextAccessor.INSTANCE.set( CTX_DEFAULT );

                final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
                    id( nodeId ).
                    editor( toBeEdited -> {
                        toBeEdited.data.addString( "newField", "fisk-" + Math.random() );
                    } ).
                    build();

                updateNode( updateNodeParams );
                nodeService.refresh( RefreshMode.BRANCH );
                nodeService.refresh( RefreshMode.VERSION );


            }
            catch ( Exception e )
            {
                errors.add( e );
            }
            finally
            {
                doneSignal.countDown();
            }
        } );
        thread.start();
    }

    private void runReader( final List<Exception> errors )
    {
        Thread thread = new Thread( () -> {
            try
            {
                startSignal.await();

                if ( !errors.isEmpty() )
                {
                    return;
                }

                final NodeVersionDiffResult diffResult = getDiff( WS_DEFAULT, WS_OTHER, nodePath );

                if ( diffResult.getTotalHits() != 1 )
                {
                    errors.add( new RuntimeException( "Active versions count: " + diffResult.getTotalHits() + ", should be: 1" ) );
                }
            }
            catch ( Exception e )
            {
                errors.add( e );
            }
            finally
            {
                doneSignal.countDown();
            }
        } );
        thread.start();
    }

    private NodeVersionDiffResult getDiff( final Branch source, final Branch target, final NodePath nodePath )
    {
        refresh();

        final FindNodesWithVersionDifferenceCommand.Builder commandBuilder = FindNodesWithVersionDifferenceCommand.create().
            searchService( this.searchService ).
            storageService( this.storageService ).
            target( target ).
            source( source );

        if ( nodePath != null )
        {
            commandBuilder.nodePath( nodePath );
        }

        return commandBuilder.build().
            execute();
    }

}
