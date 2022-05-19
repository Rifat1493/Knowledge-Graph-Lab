
import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.*;
import org.apache.jena.ontology.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.riot.*;

import java.io.FileWriter;
import java.io.IOException;

public class main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        String SOURCE = "http://www.eswc2006.org/technologies/ontology/";
        String NS = SOURCE + "#";

        OntModel m = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM_RDFS_INF );
        OntClass programme = m.createClass( NS + "Programme" );
        OntClass orgEvent = m.createClass( NS + "OrganizedEvent" );

        OntProperty hasProgramme = m.createOntProperty( NS + "hasProgramme" );

        hasProgramme.addDomain( orgEvent );
        hasProgramme.addRange( programme );
        hasProgramme.addLabel( "has programme", "en" );
        m.write(System.out);
        //out = new FileWriter( "mymodel.owl" )
        try {
            // XML format - long and verbose
            FileWriter file_obj = new FileWriter("src/main/resources/publication_rdfs.owl");
            m.write( file_obj, "RDF/XML-ABBREV" );

            // OR Turtle format - compact and more readable
            // use this variant if you're not sure which to use!
            //out = new FileWriter( "mymodel.ttl" );
            //m.write( out, "Turtle" );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
