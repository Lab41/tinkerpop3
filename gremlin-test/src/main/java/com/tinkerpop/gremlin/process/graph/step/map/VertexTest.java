package com.tinkerpop.gremlin.process.graph.step.map;

import com.tinkerpop.gremlin.LoadGraphWith;
import com.tinkerpop.gremlin.process.AbstractGremlinProcessTest;
import com.tinkerpop.gremlin.process.T;
import com.tinkerpop.gremlin.process.Traversal;
import com.tinkerpop.gremlin.structure.Direction;
import com.tinkerpop.gremlin.structure.Edge;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.util.StreamFactory;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tinkerpop.gremlin.LoadGraphWith.GraphData.CLASSIC_DOUBLE;
import static org.junit.Assert.*;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Stephen Mallette (http://traversalhen.genoprime.com)
 * @author Daniel Kuppitz (daniel at thinkaurelius.com)
 */
public abstract class VertexTest extends AbstractGremlinProcessTest {

    public abstract Traversal<Vertex, Vertex> get_g_V();

    public abstract Traversal<Vertex, Vertex> get_g_v1_out(final Object v1Id);

    public abstract Traversal<Vertex, Vertex> get_g_v2_in(final Object v2Id);

    public abstract Traversal<Vertex, Vertex> get_g_v4_both(final Object v4Id);

    public abstract Traversal<Vertex, String> get_g_v1_outX1_knowsX_name(final Object v1Id);

    public abstract Traversal<Vertex, String> get_g_V_bothX1_createdX_name();

    public abstract Traversal<Edge, Edge> get_g_E();

    public abstract Traversal<Vertex, Edge> get_g_v1_outE(final Object v1Id);

    public abstract Traversal<Vertex, Edge> get_g_v2_inE(final Object v2Id);

    public abstract Traversal<Vertex, Edge> get_g_v4_bothE(final Object v4Id);

    public abstract Traversal<Vertex, Edge> get_g_v4_bothEXcreatedX(final Object v4Id);

    public abstract Traversal<Vertex, Edge> get_g_v4_bothEX1_createdX(final Object v4Id);

    public abstract Traversal<Vertex, Edge> get_g_v4_bothEX1_knows_createdX(final Object v4Id);

    public abstract Traversal<Vertex, String> get_g_v4_bothX1X_name(final Object v4Id);

    public abstract Traversal<Vertex, String> get_g_v4_bothX2X_name(final Object v4Id);

    public abstract Traversal<Vertex, String> get_g_V_inEX2_knowsX_outV_name();

    public abstract Traversal<Vertex, Vertex> get_g_v1_outE_inV(final Object v1Id);

    public abstract Traversal<Vertex, Vertex> get_g_v2_inE_outV(final Object v2Id);

    public abstract Traversal<Vertex, Vertex> get_g_V_outE_hasXweight_1X_outV();

    public abstract Traversal<Vertex, String> get_g_V_out_outE_inV_inE_inV_both_name();

    public abstract Traversal<Vertex, String> get_g_v1_outEXknowsX_bothV_name(final Object v1Id);

    public abstract Traversal<Vertex, Vertex> get_g_v1_outXknowsX(final Object v1Id);

    public abstract Traversal<Vertex, Vertex> get_g_v1_outXknows_createdX(final Object v1Id);

    public abstract Traversal<Vertex, Vertex> get_g_v1_outEXknowsX_inV(final Object v1Id);

    public abstract Traversal<Vertex, Vertex> get_g_v1_outEXknows_createdX_inV(final Object v1Id);

    public abstract Traversal<Vertex, Vertex> get_g_V_out_out();

    public abstract Traversal<Vertex, Vertex> get_g_v1_out_out_out(final Object v1Id);

    public abstract Traversal<Vertex, String> get_g_v1_out_valueXnameX(final Object v1Id);

    public abstract Traversal<Vertex, Vertex> get_g_v1_outE_otherV(final Object v1Id);

    public abstract Traversal<Vertex, Vertex> get_g_v4_bothE_otherV(final Object v4Id);

    public abstract Traversal<Vertex, Vertex> get_g_v4_bothE_hasXweight_lt_1X_otherV(final Object v4Id);

    public abstract Traversal<Vertex, Vertex> get_g_v1_to_XOUT_knowsX(final Object v1Id);

    // VERTEX ADJACENCY

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_V() {
        final Traversal<Vertex, Vertex> traversal = get_g_V();
        printTraversalForm(traversal);
        int counter = 0;
        Set<Vertex> vertices = new HashSet<>();
        while (traversal.hasNext()) {
            counter++;
            vertices.add(traversal.next());
        }
        assertEquals(6, vertices.size());
        assertEquals(6, counter);
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_out() {
        final Traversal<Vertex, Vertex> traversal = get_g_v1_out(convertToVertexId("marko"));
        assert_g_v1_out(traversal);
    }

    private void assert_g_v1_out(final Traversal<Vertex, Vertex> traversal) {
        printTraversalForm(traversal);
        int counter = 0;
        Set<Vertex> vertices = new HashSet<>();
        while (traversal.hasNext()) {
            counter++;
            Vertex vertex = traversal.next();
            vertices.add(vertex);
            assertTrue(vertex.value("name").equals("vadas") ||
                    vertex.value("name").equals("josh") ||
                    vertex.value("name").equals("lop"));
        }
        assertEquals(3, counter);
        assertEquals(3, vertices.size());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v2_in() {
        final Traversal<Vertex, Vertex> traversal = get_g_v2_in(convertToVertexId("vadas"));
        assert_g_v2_in(traversal);
    }

    private void assert_g_v2_in(final Traversal<Vertex, Vertex> traversal) {
        printTraversalForm(traversal);
        int counter = 0;
        while (traversal.hasNext()) {
            counter++;
            assertEquals(traversal.next().value("name"), "marko");
        }
        assertEquals(1, counter);
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v4_both() {
        final Traversal<Vertex, Vertex> traversal = get_g_v4_both(convertToVertexId("josh"));
        printTraversalForm(traversal);
        int counter = 0;
        Set<Vertex> vertices = new HashSet<>();
        while (traversal.hasNext()) {
            counter++;
            Vertex vertex = traversal.next();
            vertices.add(vertex);
            assertTrue(vertex.value("name").equals("marko") ||
                    vertex.value("name").equals("ripple") ||
                    vertex.value("name").equals("lop"));
        }
        assertEquals(3, counter);
        assertEquals(3, vertices.size());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_outX1_knowsX_name() {
        final Traversal<Vertex, String> traversal = get_g_v1_outX1_knowsX_name(convertToVertexId("marko"));
        printTraversalForm(traversal);
        final String name = traversal.next();
        assertTrue(name.equals("vadas") || name.equals("josh"));
        assertFalse(traversal.hasNext());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_V_bothX1_createdX_name() {
        final Traversal<Vertex, String> traversal = get_g_V_bothX1_createdX_name();
        printTraversalForm(traversal);
        int counter = 0;
        while (traversal.hasNext()) {
            counter++;
            final String name = traversal.next();
            assertTrue(name.equals("marko") || name.equals("lop") || name.equals("josh") || name.equals("ripple") || name.equals("peter"));
        }
        assertEquals(5, counter);
    }

    // EDGE ADJACENCY

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_E() {
        final Traversal<Edge, Edge> traversal = get_g_E();
        printTraversalForm(traversal);
        int counter = 0;
        Set<Edge> edges = new HashSet<>();
        while (traversal.hasNext()) {
            counter++;
            edges.add(traversal.next());
        }
        assertEquals(6, edges.size());
        assertEquals(6, counter);
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_outE() {
        final Traversal<Vertex, Edge> traversal = get_g_v1_outE(convertToVertexId("marko"));
        printTraversalForm(traversal);
        int counter = 0;
        Set<Edge> edges = new HashSet<>();
        while (traversal.hasNext()) {
            counter++;
            Edge edge = traversal.next();
            edges.add(edge);
            assertTrue(edge.label().equals("knows") || edge.label().equals("created"));
        }
        assertEquals(3, counter);
        assertEquals(3, edges.size());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v2_inE() {
        final Traversal<Vertex, Edge> traversal = get_g_v2_inE(convertToVertexId("vadas"));
        printTraversalForm(traversal);
        int counter = 0;
        while (traversal.hasNext()) {
            counter++;
            assertEquals(traversal.next().label(), "knows");
        }
        assertEquals(1, counter);
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v4_bothEXcreateX() {
        final Traversal<Vertex, Edge> traversal = get_g_v4_bothEXcreatedX(convertToVertexId("josh"));
        printTraversalForm(traversal);
        int counter = 0;
        Set<Edge> edges = new HashSet<>();
        while (traversal.hasNext()) {
            counter++;
            Edge edge = traversal.next();
            edges.add(edge);
            assertTrue(edge.label().equals("created"));
            assertEquals(edge.outV().id().next(), convertToVertexId("josh"));
        }
        assertEquals(2, counter);
        assertEquals(2, edges.size());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v4_bothE() {
        final Traversal<Vertex, Edge> traversal = get_g_v4_bothE(convertToVertexId("josh"));
        printTraversalForm(traversal);
        int counter = 0;
        Set<Edge> edges = new HashSet<>();
        while (traversal.hasNext()) {
            counter++;
            Edge edge = traversal.next();
            edges.add(edge);
            assertTrue(edge.label().equals("knows") || edge.label().equals("created"));
        }
        assertEquals(3, counter);
        assertEquals(3, edges.size());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v4_bothEX1_createdX() {
        final Traversal<Vertex, Edge> traversal = get_g_v4_bothEX1_createdX(convertToVertexId("josh"));
        printTraversalForm(traversal);
        final Edge edge = traversal.next();
        assertEquals("created", edge.label());
        assertTrue(edge.value("weight").equals(1.0d) || edge.value("weight").equals(0.4d));
        assertFalse(traversal.hasNext());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v4_bothEX1_knows_createdX() {
        final Traversal<Vertex, Edge> traversal = get_g_v4_bothEX1_knows_createdX(convertToVertexId("josh"));
        printTraversalForm(traversal);
        final Edge edge = traversal.next();
        assertTrue(edge.label().equals("created") || edge.label().equals("knows"));
        assertTrue(edge.value("weight").equals(1.0d) || edge.value("weight").equals(0.4d));
        assertFalse(traversal.hasNext());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v4_bothX1X_name() {
        final Traversal<Vertex, String> traversal = get_g_v4_bothX1X_name(convertToVertexId("josh"));
        printTraversalForm(traversal);
        int counter = 0;
        while (traversal.hasNext()) {
            counter++;
            final String name = traversal.next();
            assertTrue(name.equals("marko") || name.equals("ripple") || name.equals("lop"));
        }
        assertEquals(1, counter);
        assertFalse(traversal.hasNext());

    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v4_bothX2X_name() {
        final Traversal<Vertex, String> traversal = get_g_v4_bothX2X_name(convertToVertexId("josh"));
        printTraversalForm(traversal);
        int counter = 0;
        while (traversal.hasNext()) {
            counter++;
            final String name = traversal.next();
            assertTrue(name.equals("marko") || name.equals("ripple") || name.equals("lop"));
        }
        assertEquals(2, counter);
        assertFalse(traversal.hasNext());
    }


    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_V_inEX2_knowsX_outV_name() {
        final Traversal<Vertex, String> traversal = get_g_V_inEX2_knowsX_outV_name();
        printTraversalForm(traversal);
        int counter = 0;
        while (traversal.hasNext()) {
            counter++;
            assertEquals(traversal.next(), "marko");
        }
        assertFalse(traversal.hasNext());
        assertEquals(2, counter);
    }

    // EDGE/VERTEX ADJACENCY

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_outE_inV() {
        final Traversal<Vertex, Vertex> traversal = get_g_v1_outE_inV(convertToVertexId("marko"));
        this.assert_g_v1_out(traversal);
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v2_inE_outV() {
        final Traversal<Vertex, Vertex> traversal = get_g_v2_inE_outV(convertToVertexId("vadas"));
        this.assert_g_v2_in(traversal);
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_V_outE_hasXweight_1X_outV() {
        final Traversal<Vertex, Vertex> traversal = get_g_V_outE_hasXweight_1X_outV();
        printTraversalForm(traversal);
        int counter = 0;
        Map<Object, Integer> counts = new HashMap<>();
        while (traversal.hasNext()) {
            final Object id = traversal.next().id();
            int previousCount = counts.getOrDefault(id, 0);
            counts.put(id, previousCount + 1);
            counter++;
        }
        assertEquals(2, counts.size());
        assertEquals(1, counts.get(convertToVertexId("marko")).intValue());
        assertEquals(1, counts.get(convertToVertexId("josh")).intValue());

        assertEquals(2, counter);
        assertFalse(traversal.hasNext());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_V_out_outE_inV_inE_inV_both_name() {
        final Traversal<Vertex, String> traversal = get_g_V_out_outE_inV_inE_inV_both_name();
        printTraversalForm(traversal);
        int counter = 0;
        Map<String, Integer> counts = new HashMap<>();
        while (traversal.hasNext()) {
            final String key = traversal.next();
            int previousCount = counts.getOrDefault(key, 0);
            counts.put(key, previousCount + 1);
            counter++;
        }
        assertEquals(3, counts.size());
        assertEquals(4, counts.get("josh").intValue());
        assertEquals(3, counts.get("marko").intValue());
        assertEquals(3, counts.get("peter").intValue());

        assertEquals(10, counter);
        assertFalse(traversal.hasNext());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_outEXknowsX_bothV_name() {
        final Traversal<Vertex, String> traversal = get_g_v1_outEXknowsX_bothV_name(convertToVertexId("marko"));
        printTraversalForm(traversal);
        List<String> names = StreamFactory.stream(traversal).collect(Collectors.toList());
        assertEquals(4, names.size());
        assertTrue(names.contains("marko"));
        assertTrue(names.contains("josh"));
        assertTrue(names.contains("vadas"));
        names.remove("marko");
        assertEquals(3, names.size());
        names.remove("marko");
        assertEquals(2, names.size());
        names.remove("josh");
        assertEquals(1, names.size());
        names.remove("vadas");
        assertEquals(0, names.size());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_outE_otherV() {
        final Traversal<Vertex, Vertex> traversal = get_g_v1_outE_otherV(convertToVertexId("marko"));
        printTraversalForm(traversal);
        int counter = 0;
        Set<Vertex> vertices = new HashSet<>();
        while (traversal.hasNext()) {
            counter++;
            Vertex vertex = traversal.next();
            vertices.add(vertex);
            assertTrue(vertex.value("name").equals("vadas") ||
                    vertex.value("name").equals("josh") ||
                    vertex.value("name").equals("lop"));
        }
        assertEquals(3, counter);
        assertEquals(3, vertices.size());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v4_bothE_outV() {
        final Traversal<Vertex, Vertex> traversal = get_g_v4_bothE_otherV(convertToVertexId("josh"));
        System.out.println("Testing: " + traversal);
        final List<Vertex> vertices = StreamFactory.stream(traversal).collect(Collectors.toList());
        assertEquals(3, vertices.size());
        assertTrue(vertices.stream().anyMatch(v -> v.value("name").equals("marko")));
        assertTrue(vertices.stream().anyMatch(v -> v.value("name").equals("ripple")));
        assertTrue(vertices.stream().anyMatch(v -> v.value("name").equals("lop")));
        assertFalse(traversal.hasNext());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v4_bothE_hasXweight_LT_1X_otherV() {
        final Traversal<Vertex, Vertex> traversal = get_g_v4_bothE_hasXweight_lt_1X_otherV(convertToVertexId("josh"));
        System.out.println("Testing: " + traversal);
        final List<Vertex> vertices = StreamFactory.stream(traversal).collect(Collectors.toList());
        assertEquals(1, vertices.size());
        assertEquals(vertices.get(0).value("name"), "lop");
        assertFalse(traversal.hasNext());
    }

    // VERTEX EDGE LABEL ADJACENCY

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_outXknowsX() {
        final Traversal<Vertex, Vertex> traversal = get_g_v1_outXknowsX(convertToVertexId("marko"));
        assert_g_v1_outXknowsX(traversal);
    }

    private void assert_g_v1_outXknowsX(Traversal<Vertex, Vertex> traversal) {
        printTraversalForm(traversal);
        int counter = 0;
        Set<Vertex> vertices = new HashSet<>();
        while (traversal.hasNext()) {
            counter++;
            Vertex vertex = traversal.next();
            vertices.add(vertex);
            assertTrue(vertex.value("name").equals("vadas") ||
                    vertex.value("name").equals("josh"));
        }
        assertEquals(2, counter);
        assertEquals(2, vertices.size());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_outXknows_createdX() {
        final Traversal<Vertex, Vertex> traversal = get_g_v1_outXknows_createdX(convertToVertexId("marko"));
        this.assert_g_v1_out(traversal);
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_outEXknowsX_inV() {
        final Traversal<Vertex, Vertex> traversal = get_g_v1_outEXknowsX_inV(convertToVertexId("marko"));
        this.assert_g_v1_outXknowsX(traversal);
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_outEXknows_createdX_inV() {
        final Traversal<Vertex, Vertex> traversal = get_g_v1_outEXknows_createdX_inV(convertToVertexId("marko"));
        this.assert_g_v1_out(traversal);
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_V_out_out() {
        final Traversal<Vertex, Vertex> traversal = get_g_V_out_out();
        printTraversalForm(traversal);
        int counter = 0;
        Set<Vertex> vertices = new HashSet<>();
        while (traversal.hasNext()) {
            counter++;
            Vertex vertex = traversal.next();
            vertices.add(vertex);
            assertTrue(vertex.value("name").equals("lop") ||
                    vertex.value("name").equals("ripple"));
        }
        assertEquals(2, counter);
        assertEquals(2, vertices.size());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_out_out_out() {
        final Traversal<Vertex, Vertex> traversal = get_g_v1_out_out_out(convertToVertexId("marko"));
        assertFalse(traversal.hasNext());
    }

    // PROPERTY TESTING

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_out_propertyXnameX() {
        final Traversal<Vertex, String> traversal = get_g_v1_out_valueXnameX(convertToVertexId("marko"));
        printTraversalForm(traversal);
        int counter = 0;
        Set<String> names = new HashSet<>();
        while (traversal.hasNext()) {
            counter++;
            String name = traversal.next();
            names.add(name);
            assertTrue(name.equals("vadas") ||
                    name.equals("josh") ||
                    name.equals("lop"));
        }
        assertEquals(3, counter);
        assertEquals(3, names.size());
    }

    @Test
    @LoadGraphWith(CLASSIC_DOUBLE)
    public void g_v1_to_XOUT_knowsX() {
        final Traversal<Vertex, Vertex> traversal = get_g_v1_to_XOUT_knowsX(convertToVertexId("marko"));
        printTraversalForm(traversal);
        int counter = 0;
        while (traversal.hasNext()) {
            counter++;
            Vertex vertex = traversal.next();
            String name = vertex.value("name");
            assertTrue(name.equals("vadas") ||
                    name.equals("josh"));
        }
        assertEquals(2, counter);
        assertFalse(traversal.hasNext());

    }

    public static class JavaVertexTest extends VertexTest {
        public JavaVertexTest() {
            requiresGraphComputer = false;
        }

        public Traversal<Vertex, Vertex> get_g_V() {
            return g.V();
        }

        public Traversal<Vertex, Vertex> get_g_v1_out(final Object v1Id) {
            return g.v(v1Id).out();
        }

        public Traversal<Vertex, Vertex> get_g_v2_in(final Object v2Id) {
            return g.v(v2Id).in();
        }

        public Traversal<Vertex, Vertex> get_g_v4_both(final Object v4Id) {
            return g.v(v4Id).both();
        }

        public Traversal<Vertex, String> get_g_v1_outX1_knowsX_name(final Object v1Id) {
            return g.v(v1Id).out(1, "knows").value("name");
        }

        public Traversal<Vertex, String> get_g_V_bothX1_createdX_name() {
            return g.V().both(1, "created").value("name");
        }

        public Traversal<Edge, Edge> get_g_E() {
            return g.E();
        }

        public Traversal<Vertex, Edge> get_g_v1_outE(final Object v1Id) {
            return g.v(v1Id).outE();
        }

        public Traversal<Vertex, Edge> get_g_v2_inE(final Object v2Id) {
            return g.v(v2Id).inE();
        }

        public Traversal<Vertex, Edge> get_g_v4_bothE(final Object v4Id) {
            return g.v(v4Id).bothE();
        }

        public Traversal<Vertex, Edge> get_g_v4_bothEXcreatedX(final Object v4Id) {
            return g.v(v4Id).bothE("created");
        }

        public Traversal<Vertex, Edge> get_g_v4_bothEX1_createdX(final Object v4Id) {
            return g.v(v4Id).bothE(1, "created");
        }

        public Traversal<Vertex, Edge> get_g_v4_bothEX1_knows_createdX(final Object v4Id) {
            return g.v(v4Id).bothE(1, "knows", "created");
        }

        public Traversal<Vertex, String> get_g_v4_bothX1X_name(final Object v4Id) {
            return g.v(v4Id).both(1).value("name");
        }

        public Traversal<Vertex, String> get_g_v4_bothX2X_name(final Object v4Id) {
            return g.v(v4Id).both(2).value("name");
        }

        public Traversal<Vertex, String> get_g_V_inEX2_knowsX_outV_name() {
            return g.V().inE(2, "knows").outV().value("name");
        }

        public Traversal<Vertex, Vertex> get_g_v1_outE_inV(final Object v1Id) {
            return g.v(v1Id).outE().inV();
        }

        public Traversal<Vertex, Vertex> get_g_v2_inE_outV(final Object v2Id) {
            return g.v(v2Id).inE().outV();
        }

        public Traversal<Vertex, Vertex> get_g_V_outE_hasXweight_1X_outV() {
            return g.V().outE().has("weight", 1.0d).outV();
        }

        public Traversal<Vertex, String> get_g_V_out_outE_inV_inE_inV_both_name() {
            return g.V().out().outE().inV().inE().inV().both().value("name");
        }

        public Traversal<Vertex, String> get_g_v1_outEXknowsX_bothV_name(final Object v1Id) {
            return g.v(v1Id).outE("knows").bothV().value("name");
        }

        public Traversal<Vertex, Vertex> get_g_v1_outXknowsX(final Object v1Id) {
            return g.v(v1Id).out("knows");
        }

        public Traversal<Vertex, Vertex> get_g_v1_outXknows_createdX(final Object v1Id) {
            return g.v(v1Id).out("knows", "created");
        }

        public Traversal<Vertex, Vertex> get_g_v1_outEXknowsX_inV(final Object v1Id) {
            return g.v(v1Id).outE("knows").inV();
        }

        public Traversal<Vertex, Vertex> get_g_v1_outEXknows_createdX_inV(final Object v1Id) {
            return g.v(v1Id).outE("knows", "created").inV();
        }

        public Traversal<Vertex, Vertex> get_g_v1_outE_otherV(final Object v1Id) {
            return g.v(v1Id).outE().otherV();
        }

        public Traversal<Vertex, Vertex> get_g_v4_bothE_otherV(final Object v4Id) {
            return g.v(v4Id).bothE().otherV();
        }

        public Traversal<Vertex, Vertex> get_g_v4_bothE_hasXweight_lt_1X_otherV(Object v4Id) {
            return g.v(v4Id).bothE().has("weight", T.lt, 1d).otherV();
        }

        public Traversal<Vertex, Vertex> get_g_V_out_out() {
            return g.V().out().out();
        }

        public Traversal<Vertex, Vertex> get_g_v1_out_out_out(final Object v1Id) {
            return g.v(v1Id).out().out().out();
        }

        public Traversal<Vertex, String> get_g_v1_out_valueXnameX(final Object v1Id) {
            return g.v(v1Id).out().value("name");
        }

        public Traversal<Vertex, Vertex> get_g_v1_to_XOUT_knowsX(final Object v1Id) {
            return g.v(v1Id).to(Direction.OUT, "knows");
        }
    }

    // todo: some of the graph computer tests do not pass

    public static class JavaComputerVertexTest extends VertexTest {
        public JavaComputerVertexTest() {
            requiresGraphComputer = true;
        }

        public Traversal<Vertex, Vertex> get_g_V() {
            return g.V().submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v1_out(final Object v1Id) {
            return g.v(v1Id).out().submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v2_in(final Object v2Id) {
            return g.v(v2Id).in().submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v4_both(final Object v4Id) {
            return g.v(v4Id).both().submit(g.compute());
        }

        public Traversal<Vertex, String> get_g_v1_outX1_knowsX_name(final Object v1Id) {
            return g.v(v1Id).out(1, "knows").<String>value("name").submit(g.compute());
        }

        public Traversal<Vertex, String> get_g_V_bothX1_createdX_name() {
            return g.V().both(1, "created").<String>value("name").submit(g.compute());
        }

        public Traversal<Vertex, String> get_g_v4_bothX1X_name(final Object v4Id) {
            return g.v(v4Id).both(1).<String>value("name").submit(g.compute());
        }

        public Traversal<Vertex, String> get_g_v4_bothX2X_name(final Object v4Id) {
            return g.v(v4Id).both(2).<String>value("name").submit(g.compute());
        }

        public Traversal<Edge, Edge> get_g_E() {
            return g.E().submit(g.compute());
        }

        public Traversal<Vertex, Edge> get_g_v1_outE(final Object v1Id) {
            return g.v(v1Id).outE().submit(g.compute());
        }

        public Traversal<Vertex, Edge> get_g_v2_inE(final Object v2Id) {
            return g.v(v2Id).inE().submit(g.compute());
        }

        public Traversal<Vertex, Edge> get_g_v4_bothE(final Object v4Id) {
            return g.v(v4Id).bothE().submit(g.compute());
        }

        public Traversal<Vertex, Edge> get_g_v4_bothEXcreatedX(final Object v4Id) {
            return g.v(v4Id).bothE("created").submit(g.compute());
        }

        public Traversal<Vertex, Edge> get_g_v4_bothEX1_createdX(final Object v4Id) {
            return g.v(v4Id).bothE(1, "created").submit(g.compute());
        }

        public Traversal<Vertex, Edge> get_g_v4_bothEX1_knows_createdX(final Object v4Id) {
            return g.v(v4Id).bothE(1, "knows", "created").submit(g.compute());
        }


        public Traversal<Vertex, String> get_g_V_inEX2_knowsX_outV_name() {
            return g.V().inE(2, "knows").outV().<String>value("name").submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v1_outE_inV(final Object v1Id) {
            return g.v(v1Id).outE().inV().submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v2_inE_outV(final Object v2Id) {
            return g.v(v2Id).inE().outV().submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_V_outE_hasXweight_1X_outV() {
            return g.V().outE().has("weight", 1.0d).outV().submit(g.compute());
        }

        public Traversal<Vertex, String> get_g_V_out_outE_inV_inE_inV_both_name() {
            return g.V().out().outE().inV().inE().inV().both().<String>value("name").submit(g.compute());
        }

        public Traversal<Vertex, String> get_g_v1_outEXknowsX_bothV_name(final Object v1Id) {
            return g.v(v1Id).outE("knows").bothV().<String>value("name").submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v1_outXknowsX(final Object v1Id) {
            return g.v(v1Id).out("knows").submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v1_outXknows_createdX(final Object v1Id) {
            return g.v(v1Id).out("knows", "created").submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v1_outEXknowsX_inV(final Object v1Id) {
            return g.v(v1Id).outE("knows").inV().submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v1_outEXknows_createdX_inV(final Object v1Id) {
            return g.v(v1Id).outE("knows", "created").inV().submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v1_outE_otherV(final Object v1Id) {
            return g.v(v1Id).outE().otherV().submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v4_bothE_otherV(final Object v4Id) {
            return g.v(v4Id).bothE().otherV().submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v4_bothE_hasXweight_lt_1X_otherV(Object v4Id) {
            return g.v(v4Id).bothE().has("weight", T.lt, 1d).otherV().submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_V_out_out() {
            return g.V().out().out().submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v1_out_out_out(final Object v1Id) {
            return g.v(v1Id).out().out().out().submit(g.compute());
        }

        public Traversal<Vertex, String> get_g_v1_out_valueXnameX(final Object v1Id) {
            return g.v(v1Id).out().<String>value("name").submit(g.compute());
        }

        public Traversal<Vertex, Vertex> get_g_v1_to_XOUT_knowsX(final Object v1Id) {
            return g.v(v1Id).to(Direction.OUT, "knows").submit(g.compute());
        }
    }
}
