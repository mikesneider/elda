package com.epimorphics.lda.renderers.velocity.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.epimorphics.lda.renderers.velocity.WrappedNode;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class TestVelocityItems {

	static final String NS = "http://junit/epimorphics.com/ns#";
	
	Model m = ModelFactory.createDefaultModel();
	
	@Test public void ensureItemPreservesResourceURI() {
		Resource r = m.createResource( NS + "leafName" );
		WrappedNode i = new WrappedNode( r );
		assertEquals( r.getURI(), i.getURI() );
	}
	
}
