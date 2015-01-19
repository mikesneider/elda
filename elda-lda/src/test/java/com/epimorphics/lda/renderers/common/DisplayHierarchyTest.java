/*****************************************************************************
 * Elda project https://github.com/epimorphics/elda
 * LDA spec: http://code.google.com/p/linked-data-api/
 *
 * Copyright (c) 2014 Epimorphics Ltd. All rights reserved.
 * Licensed under the Apache Software License 2.0.
 * Full license: https://raw.githubusercontent.com/epimorphics/elda/master/LICENCE
 *****************************************************************************/

package com.epimorphics.lda.renderers.common;


import static org.junit.Assert.*;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.*;

import com.epimorphics.jsonrdf.utils.ModelIOUtils;
import com.epimorphics.rdfutil.ModelWrapper;
import com.epimorphics.rdfutil.RDFNodeWrapper;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Unit tests for {@link DisplayHierarchy}
 *
 * @author Ian Dickinson, Epimorphics (mailto:ian@epimorphics.com)
 */
public class DisplayHierarchyTest
{
    /***********************************/
    /* Constants                       */
    /***********************************/

    /***********************************/
    /* Static variables                */
    /***********************************/

    static final Model apiMetadataModel = ModelFactory.createDefaultModel();
    static final Model apiObjectModel = ModelFactory.createDefaultModel();
    static final Model apiResultsModel = ModelIOUtils.modelFromTurtle( Fixtures.COMMON_PREFIXES + Fixtures.PAGE_BWQ );

    static final String view_uri = "http://environment.data.gov.uk/doc/bathing-water-quality/in-season/latest.ttl?_lang=en,cy&_view=salmonella&_metadata=all&_page=0";

    /***********************************/
    /* Instance variables              */
    /***********************************/

    @Rule public JUnitRuleMockery context = new JUnitRuleMockery() {{
        // we are forced to use the legacy imposteriser because APIResultSet does not
        // have an interface that it conforms to
        setImposteriser(ClassImposteriser.INSTANCE);

        setThreadingPolicy(new Synchroniser());
    }};

    private DisplayHierarchy dh;

    /***********************************/
    /* Constructors                    */
    /***********************************/

    /***********************************/
    /* External signature methods      */
    /***********************************/

    @Before
    public void before() {
        ResultsModel rm = new ResultsModel( Fixtures.mockResultSet( context, apiResultsModel, apiObjectModel, apiMetadataModel ) );
        Page page = rm.page();
        page.initialiseShortNameRenderer( Fixtures.shortNameServiceFixture() );

        dh = new DisplayHierarchy( page );
    }

    @Test
    public void testExpand() {
        dh.expand();
        assertEquals( 10, dh.roots().size() );
    }

    @Test
    public void testContextSeen() {
        DisplayHierarchy.DisplayHierarchyContext ctx = new DisplayHierarchy.DisplayHierarchyContext();
        Model m = ModelFactory.createDefaultModel();
        ModelWrapper mw = new ModelWrapper( m );
        RDFNodeWrapper rn = new RDFNodeWrapper( mw, m.createResource( "http://example.com/foo" ));
        
        assertFalse( ctx.isSeen( rn ) );
        ctx.see( rn );
        assertTrue( ctx.isSeen( rn ) );
    }

    /***********************************/
    /* Internal implementation methods */
    /***********************************/

    /***********************************/
    /* Inner class definitions         */
    /***********************************/

}

