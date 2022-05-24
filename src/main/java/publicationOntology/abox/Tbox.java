package publicationOntology.abox;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.*;
import org.apache.jena.ontology.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.riot.*;

import java.io.FileWriter;
import java.io.IOException;

import static org.apache.jena.vocabulary.RDFSyntax.literal;

public class Tbox {
    public static void createRDFS() {
        // Name space for my model
        String NS = "http://www.gra.fo/publication/";

        // The OntModelSpec.RDFS_MEM_RDFS_INF: gives us rule reasoner with RDFS-level entailment-rules
        OntModel m = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM_RDFS_INF );

        // Classes
        OntClass full_paper = m.createClass( NS + "Full_paper" );
        OntClass short_paper = m.createClass( NS + "Short_paper" );
        OntClass demo_paper = m.createClass( NS + "Demo_paper");
        OntClass poster = m.createClass( NS + "Poster" );
        OntClass paper = m.createClass( NS + "Paper" );

        OntClass author = m.createClass( NS + "Author" );
        OntClass person = m.createClass( NS + "Person" );
        OntClass venue = m.createClass( NS + "Venue" );
        OntClass conference = m.createClass( NS + "Conference" );
        OntClass journal = m.createClass( NS + "Journal" );

        OntClass company = m.createClass( NS + "Company" );
        OntClass university = m.createClass( NS + "University" );
        OntClass organization = m.createClass( NS + "Organization" );

        OntClass review = m.createClass( NS + "Review");

        OntClass keyword = m.createClass( NS + "Keyword" );
        OntClass database = m.createClass(NS + "Database");
        OntClass machine_learning = m.createClass(NS + "Machine_learning");
        OntClass cyber_security = m.createClass(NS + "Cyber_security");



        // Connecting Classes to sub-Classes
        paper.addSubClass( poster );
        paper.addSubClass( demo_paper );
        paper.addSubClass( short_paper );
        paper.addSubClass( full_paper );

        venue.addSubClass( conference );
        venue.addSubClass( journal );

        organization.addSubClass(company);
        organization.addSubClass(company);

        keyword.addSubClass(machine_learning);
        keyword.addSubClass(database);
        keyword.addSubClass(cyber_security);

        person.addSubClass( author );

        // Properties
        OntProperty contains = m.createOntProperty(NS + "contains");
        contains.addDomain( paper );
        contains.addRange( keyword );
        OntProperty gives_review = m.createOntProperty(NS + "gives_review");
        gives_review.addDomain( author );
        gives_review.addRange( review );

        OntProperty handled_byc = m.createOntProperty(NS + "handled_byc");
        handled_byc.addDomain( conference );
        handled_byc.addRange( person );

        OntProperty handled_byj = m.createOntProperty(NS + "handled_byj");
        handled_byj.addDomain( journal );
        handled_byj.addRange( person );



        OntProperty published_in = m.createOntProperty(NS + "published_in");
        published_in.addDomain( paper );
        published_in.addRange( venue );


        OntProperty submitted_for = m.createOntProperty(NS + "submitted_for");
        submitted_for.addDomain( paper );
        submitted_for.addRange( review );

        OntProperty works_in = m.createOntProperty(NS + "works_in");
        works_in.addDomain( author );
        works_in.addRange( organization);

        OntProperty written_by = m.createOntProperty(NS + "written_by");
        written_by.addDomain( paper );
        written_by.addRange( author );

        // Literal data type property

        OntProperty business = m.createOntProperty(NS + "business");
        business.addDomain( company );
        business.addRange( RDFS.Literal );

        OntProperty comment = m.createOntProperty(NS + "comment");
        comment.addDomain( review );
        comment.addRange( RDFS.Literal );


        OntProperty confName = m.createOntProperty(NS + "confName");
        confName.addDomain( conference );
        confName.addRange( RDFS.Literal );


        OntProperty confPublisher = m.createOntProperty(NS + "confPublisher");
        confPublisher.addDomain( conference );
        confPublisher.addRange( RDFS.Literal );


        OntProperty jourName = m.createOntProperty(NS + "jourName");
        jourName.addDomain( journal );
        jourName.addRange( RDFS.Literal );


        OntProperty journalPublisher = m.createOntProperty(NS + "journalPublisher");
        journalPublisher.addDomain( journal );
        journalPublisher.addRange( RDFS.Literal );

        OntProperty demo_link = m.createOntProperty(NS + "demo_link");
        demo_link.addDomain( demo_paper );
        demo_link.addRange( RDFS.Literal );

        OntProperty firstName = m.createOntProperty(NS + "firstName");
        firstName.addDomain( author );
        firstName.addRange( RDFS.Literal );

        OntProperty lastName = m.createOntProperty(NS + "lastName");
        lastName.addDomain( author );
        lastName.addRange( RDFS.Literal );

        OntProperty homepage = m.createOntProperty(NS + "homepage");
        homepage.addDomain( university );
        homepage.addRange( RDFS.Literal );

        OntProperty long_page_size = m.createOntProperty(NS + "long_page_size");
        long_page_size.addDomain( full_paper );
        long_page_size.addRange( RDFS.Literal );

        OntProperty name = m.createOntProperty(NS + "name");
        name.addDomain( university );
        name.addDomain( company );
        name.addRange( RDFS.Literal );

        OntProperty poster_link = m.createOntProperty(NS + "poster_link");
        poster_link.addDomain( poster );
        poster_link.addRange( RDFS.Literal );


        OntProperty publication_year = m.createOntProperty(NS + "publication_year");
        publication_year.addDomain( paper );
        publication_year.addRange( RDFS.Literal );

        OntProperty title = m.createOntProperty(NS + "title");
        title.addDomain( paper );
        title.addRange( RDFS.Literal );


        OntProperty short_page_size = m.createOntProperty(NS + "short_page_size");
        short_page_size.addDomain( short_paper );
        short_page_size.addRange( RDFS.Literal );


        // Write the model in XML format to the std-out
        // m.write(System.out);
        try {
            // XML format - long and verbose
            FileWriter file_obj = new FileWriter(Config.OUTPUT_PATH+"/publication_rdfs.owl");
            m.write(file_obj, "RDF/XML-ABBREV" );

            // OR Turtle format - compact and more readable
            // use this variant if you're not sure which to use!
            // out = new FileWriter( "model.ttl" );
            //m.write( out, "Turtle" );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
