package com.epimorphics.lda.xml_rendering_regression.tests;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.XMLTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.epimorphics.lda.bindings.Bindings;
import com.epimorphics.lda.core.APIResultSet.MergedModels;
import com.epimorphics.lda.core.EndpointMetadata;
import com.epimorphics.lda.core.SetsMetadata;
import com.epimorphics.lda.query.WantsMetadata;
import com.epimorphics.lda.renderers.Factories;
import com.epimorphics.lda.renderers.Factories.FormatNameAndType;
import com.epimorphics.lda.renderers.XMLRenderer;
import com.epimorphics.lda.shortnames.NameMap;
import com.epimorphics.lda.shortnames.ShortnameService;
import com.epimorphics.lda.shortnames.StandardShortnameService;
import com.epimorphics.lda.sources.SparqlSource;
import com.epimorphics.lda.specs.EndpointDetails;
import com.epimorphics.util.CollectionUtils;
import com.epimorphics.util.DOMUtils;
import com.epimorphics.util.URIUtils;
import com.epimorphics.vocabs.API;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class TestRenderingExperiments extends XMLTestCase {
	
	static class Blob {
		
		private static final String goldRoot = "src/test/resources/xml_gold/";
		
		final Model objectModel;
		final String expected_xml;
		
		Blob( Model objectModel, String expected_xml ) {
			this.objectModel = objectModel;
			this.expected_xml = expected_xml;
		}
		
		static Blob load( String name ) {
			Model objectModel = FileManager.get().loadModel( goldRoot + name + "/object_model.ttl" );
			String expected_xml = FileManager.get().readWholeFileAsUTF8( goldRoot + name + "/rendered.xml" );
			return new Blob( objectModel, expected_xml );
		}
	}
	
	public void testIt() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		Blob b = Blob.load( "probe" );
		Model objectModel = b.objectModel;
	//	
		final MergedModels mm = new MergedModels( objectModel );
	//
		SetsMetadata setsMeta = new SetsMetadata() {
			@Override public void setMetadata(String type, Model meta) {
				mm.getMetaModel().add( meta );
			}
		};
	//
		WantsMetadata wantsMeta = new WantsMetadata() {
			@Override public boolean wantsMetadata(String name) {
				return true;
			}
		};
	//
		EndpointDetails details = new EndpointDetails() {
			@Override public boolean isListEndpoint() {
				return true;
			}
			
			@Override public boolean hasParameterBasedContentNegotiation() {
				return false;
			}
		};
	//
		List<Resource> resultList = new ArrayList<Resource>();
		String pre = "http://landregistry.data.gov.uk/data/ppi/transaction/";
		resultList.add( objectModel.createResource( pre + "AB393402-2265-4A0A-81CA-64523DF04878" ) );
		resultList.add( objectModel.createResource( pre + "5001880F-F082-4424-9B11-F5771FBA6006" ) );
		resultList.add( objectModel.createResource( pre + "056E052F-4193-4FBF-9956-24D46E9CCBA5" ) );
		resultList.add( objectModel.createResource( pre + "3B3E9500-D094-4747-9434-542A81F1990B" ) );
		resultList.add( objectModel.createResource( pre + "7235D3F7-3B24-42C2-ADEE-20BA628886A2" ) );
		resultList.add( objectModel.createResource( pre + "9DA12BDB-3497-49F9-93DE-47AE922A2FD7" ) );
		resultList.add( objectModel.createResource( pre + "407F87D5-E54B-4BA3-8CB6-455C96109B63" ) );
		resultList.add( objectModel.createResource( pre + "4EC1D2A7-B6E4-4D08-8A36-61BCB38AD8B8" ) );
		resultList.add( objectModel.createResource( pre + "01D708E7-F982-4A19-9A55-AEA000CB4426" ) );
		resultList.add( objectModel.createResource( pre + "4CE0C18F-C8F3-4897-87C4-9721E32BEE16" ) ); 
	//
		URI ru = URIUtils.newURI( "http://landregistry.data.gov.uk/data/ppi/transaction" );
		Resource uriForDefinition = mm.getMetaModel().createResource( "http://landregistry.data.gov.uk/meta/data/ppi/transaction" );
	//
		Resource thisMetaPage = mm.getMetaModel().createResource( ru.toString() );
	//	
		Set<FormatNameAndType> formats = new HashSet<FormatNameAndType>();
		formats.add( new FormatNameAndType( "csv", "text/csv" ) );
		formats.add( new FormatNameAndType( "html", "text/html" ) );
		formats.add( new FormatNameAndType( "json", "application/json" ) );
		formats.add( new FormatNameAndType( "rdf", "application/rdf+xml" ) );
		formats.add( new FormatNameAndType( "text", "text/plain" ) );
		formats.add( new FormatNameAndType( "ttl", "text/turtle" ) );
		formats.add( new FormatNameAndType( "xml", "application/xml" ) );
	//	
	//
		PrefixMapping pm = PrefixMapping.Factory.create();
		Model maps = ModelFactory.createDefaultModel();
		
		maps
			.createResource( "http://landregistry.data.gov.uk/def/ppi/hasTransactionRecord" )
			.addProperty( API.label, "hasTransactionRecord" )
			;
		maps
			.createResource( "http://landregistry.data.gov.uk/def/ppi/transactionId" )
			.addProperty( API.label, "transactionId" )
			;
		maps
			.createResource( "http://landregistry.data.gov.uk/def/ppi/hasTransactionRecord" )
			.addProperty( API.label, "hasTransactionRecord" )
			;
		maps
			.createResource( "http://landregistry.data.gov.uk/def/ppi/hasTransactionRecord" )
			.addProperty( API.label, "hasTransactionRecord" )
			;
		maps
			.createResource( "http://landregistry.data.gov.uk/def/ppi/hasTransactionRecord" )
			.addProperty( API.label, "hasTransactionRecord" )
			;
		maps
			.createResource( "http://landregistry.data.gov.uk/def/ppi/hasTransactionRecord" )
			.addProperty( API.label, "hasTransactionRecord" )
			;
		maps
			.createResource( "http://landregistry.data.gov.uk/def/ppi/hasTransactionRecord" )
			.addProperty( API.label, "hasTransactionRecord" )
			;

		StandardShortnameService sns = new StandardShortnameService( maps );
		
		NameMap nameMap = sns.nameMap(); // new NameMap();
//		nameMap.load( pm, maps );
	//
		EndpointMetadata.addAllMetadata
	    	( mm
	    	, ru
	    	, uriForDefinition
	    	, new Bindings()
	    	, nameMap
	    	, true
	    	, thisMetaPage
	    	, 0
	    	, 10
	    	, true
	    	, resultList
	    	, setsMeta
	    	, wantsMeta
	    	, "select query"
	    	, "view query"
	    	, new SparqlSource( null, "sparql:endpoint", null )
	    	, CollectionUtils.set( "all", "basic", "description", "none" )
	    	, formats
	    	, details
	    	); 
						
		
		Resource root = mm.getMetaModel().createResource( ru.toString() );
		
		Document d = DOMUtils.newDocument();
		
		XMLRenderer r = new XMLRenderer( sns );
		r.renderInto( root, mm, d, false );
		
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
		transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource( d );
		StreamResult result = new StreamResult(System.out);
		transformer.transform( source, result );
		
		// Document expected = parse("<result href='http://localhost:8080/elda/something/or/other' version='0.2' format='linked-data-api'><primaryTopic href='primary:topic'><label>primary</label></primaryTopic></result>");
		Document expected = parse( b.expected_xml );
		assertXMLEqual( expected, d );
	}

	public Document parse( String data ) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document parsed = dBuilder.parse( new InputSource( new StringReader( data ) ) );
		removeWhitespaceNodes( parsed.getDocumentElement() );
		return parsed;
	}
	
	public static void removeWhitespaceNodes( Element e ) {
		NodeList children = e.getChildNodes();
		for (int i = children.getLength() - 1; i >= 0; i--) {
			Node child = children.item(i);
			if (child instanceof Text && ((Text) child).getData().trim().length() == 0) {
				e.removeChild(child);
			} else if (child instanceof Element) {
				removeWhitespaceNodes((Element) child);
			}
		}
	}

}
