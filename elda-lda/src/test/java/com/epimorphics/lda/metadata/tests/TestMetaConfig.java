package com.epimorphics.lda.metadata.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import com.epimorphics.jsonrdf.utils.ModelIOUtils;
import com.epimorphics.lda.core.EndpointMetadata;
import com.epimorphics.lda.metadata.MetaConfig;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;

public class TestMetaConfig {

	@Ignore @Test public void testEmptyConfig() {
		assertEquals(false, new MetaConfig().disableDefaultMetadata());
		assertEquals(false, new MetaConfig(false).disableDefaultMetadata());
		assertEquals(true, new MetaConfig(true).disableDefaultMetadata());

		for (Property p: EndpointMetadata.hardwiredProperties)
			assertEquals(false, new MetaConfig(false).drop(p));
		
		for (Property p: EndpointMetadata.hardwiredProperties)
			assertEquals(true, new MetaConfig(true).drop(p));
		
		for (Property p: EndpointMetadata.hardwiredProperties)
			assertEquals(false, new MetaConfig().drop(p));
	}
	
	protected String configString(String control) {
		return TestParseMetadataConfig.longString
			( TestParseMetadataConfig.apiPrefixString
			, TestParseMetadataConfig.eldaPrefixString
			, TestParseMetadataConfig.rdfPrefixString
			, TestParseMetadataConfig.rdfsPrefixString
			, "@prefix : <eh:/> ."
			, ""
			, "<eh:/root> a api:API"
			, control
			, "; elda:enable-default-metadata ("
			, "<http://purl.org/dc/terms/hasPart>"
			, "<http://purl.org/dc/terms/isPartOf>"
			, ")"
			, "."
			);
	}
	
	@Ignore @Test public void testEnablePropertiesConfigAbsent() {
		testEnablePropertiesConfig(false, false, "");
	}
		
	@Ignore @Test public void testEnablePropertiesConfigFalse() {
		testEnablePropertiesConfig(false, false, "; elda:disable-default-metadata false");
	}
		
	@Ignore @Test public void testEnablePropertiesConfigTrue() {
		testEnablePropertiesConfig(false, true, "; elda:disable-default-metadata true");
	}
		
	public void testEnablePropertiesConfig(boolean expectHardwired, boolean expectNotwired, String control) {
		Model config = ModelIOUtils.modelFromTurtle(configString(control));
		Resource root = config.createResource("eh:/root");
		MetaConfig mc = new MetaConfig(root);
		assertEquals(expectHardwired, mc.drop(config.createProperty("http://purl.org/dc/terms/hasPart")));
		assertEquals(expectNotwired, mc.drop(config.createProperty("eh:/not-at-all")));
	}
	protected String configNamedBlock() {
		return TestParseMetadataConfig.longString
			( TestParseMetadataConfig.apiPrefixString
			, TestParseMetadataConfig.eldaPrefixString
			, TestParseMetadataConfig.rdfPrefixString
			, TestParseMetadataConfig.rdfsPrefixString
			, "@prefix : <eh:/> ."
			, ""
			, "<eh:/root> a api:API"
			, "; elda:metadata ["
			, "    api:name \"NameX\""
			, "    ; <eh:/P> 10"
			, "    ; <eh:/Q> <eh:/O>"
			, "    ]"
			, "."
			);
	}
	
	@Test public void testBuildMetadataBlock() {
		Model config = ModelIOUtils.modelFromTurtle(configNamedBlock());
		Resource root = config.createResource("eh:/root");
		
		MetaConfig mc = new MetaConfig(root);
		
		Model meta = ModelFactory.createDefaultModel();
		Resource metaRoot = meta.createResource("eh:/result");
		mc.addMetadata(metaRoot);
		
		String expectString = "<eh:/result> <eh:/P> 10; <eh:/Q> <eh:/O>.";
		Model expect = ModelIOUtils.modelFromTurtle(expectString);
		ModelTestBase.assertIsoModels("", expect, meta);
	}
	
}
