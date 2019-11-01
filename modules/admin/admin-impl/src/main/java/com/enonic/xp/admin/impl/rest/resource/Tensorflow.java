package com.enonic.xp.admin.impl.rest.resource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

public class Tensorflow
{
    public static String classify( byte[] imageBytes )
    {
        try
        {
            String modelpath = "\\Users\\sry\\Downloads\\inception_dec_2015";
            byte[] graphDef = Files.readAllBytes( Paths.get( modelpath, "tensorflow_inception_graph.pb" ) );
            List<String> labels =
                Files.readAllLines( Paths.get( modelpath, "imagenet_comp_graph_label_strings.txt" ), StandardCharsets.UTF_8 );

                float[] labelProbabilities = executeInceptionGraph( graphDef, imageBytes );
                int bestLabelIdx = maxIndex( labelProbabilities );
                return labels.get( bestLabelIdx );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private static float[] executeInceptionGraph( byte[] graphDef, byte[] imageBytes )
    {
        try (final Tensor image = Tensor.create( imageBytes ); final Graph g = new Graph())
        {
            g.importGraphDef( graphDef );

            try (final Session s = new Session( g );
                 final Tensor result = s.runner().feed( "DecodeJpeg/contents", image ).fetch( "softmax" ).run().get( 0 ))
            {
                final long[] rshape = result.shape();
                if ( result.numDimensions() != 2 || rshape[0] != 1 )
                {
                    throw new RuntimeException( String.format(
                        "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                        Arrays.toString( rshape ) ) );
                }
                int numlabels = (int) rshape[1];
                return ( (float[][]) result.copyTo( new float[1][numlabels] ) )[0];
            }
        }
    }

    private static int maxIndex( float[] probabilities )
    {
        int best = 0;
        for ( int i = 1; i < probabilities.length; i++ )
        {
            if ( probabilities[i] > probabilities[best] )
            {
                best = i;
            }
        }
        return best;
    }

}
