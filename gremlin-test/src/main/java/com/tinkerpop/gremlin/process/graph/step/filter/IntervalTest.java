package com.tinkerpop.gremlin.process.graph.step.filter;

import com.tinkerpop.gremlin.LoadGraphWith;
import com.tinkerpop.gremlin.process.AbstractGremlinProcessTest;
import com.tinkerpop.gremlin.process.Traversal;
import com.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import static com.tinkerpop.gremlin.LoadGraphWith.GraphData.MODERN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public abstract class IntervalTest extends AbstractGremlinProcessTest {

    public abstract Traversal<Vertex, Vertex> get_g_v1_outE_intervalXweight_0_06X_inV(final Object v1Id);

    @Test
    @LoadGraphWith(MODERN)
    public void g_v1_outE_intervalXweight_0_06X_inV() {
        final Traversal<Vertex, Vertex> traversal = get_g_v1_outE_intervalXweight_0_06X_inV(convertToVertexId("marko"));
        printTraversalForm(traversal);
        while (traversal.hasNext()) {
            Vertex vertex = traversal.next();
            assertTrue(vertex.value("name").equals("vadas") || vertex.value("name").equals("lop"));
        }
        assertFalse(traversal.hasNext());
    }

    public static class StandardTest extends IntervalTest {
        public StandardTest() {
            requiresGraphComputer = false;
        }

        @Override
        public Traversal<Vertex, Vertex> get_g_v1_outE_intervalXweight_0_06X_inV(final Object v1Id) {
            return g.v(v1Id).outE().interval("weight", 0.0d, 0.6d).inV();
        }
    }

    public static class ComputerTest extends IntervalTest {
        public ComputerTest() {
            requiresGraphComputer = true;
        }

        @Override
        public Traversal<Vertex, Vertex> get_g_v1_outE_intervalXweight_0_06X_inV(final Object v1Id) {
            return g.v(v1Id).outE().interval("weight", 0.0d, 0.6d).inV().submit(g.compute());
        }
    }
}
