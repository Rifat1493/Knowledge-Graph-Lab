package publicationOntology.abox;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;


import java.io.*;
import java.util.Random;
import java.util.*;

public class Creator {

    public static void createALL() throws IOException {

        //Company
        Model companies_model = ModelFactory.createDefaultModel();

        BufferedReader companies_csvReader = new BufferedReader(new FileReader(Config.COMPANIES_PATH));
        String companies_row;
        while ((companies_row = companies_csvReader.readLine()) != null) {
            String[] row_data = companies_row.split(";");

            String companyName = row_data[0];
            String business = row_data[1];

            String companyUri = companyName.replaceAll("[^\\p{IsAlphabetic}]", "_");
            Resource currentCompany = companies_model.createResource(Config.RESOURCE_URL + companyUri)
                    .addProperty(RDF.type, companies_model.getResource("http://www.gra.fo/publication/Company"))
                    .addProperty(companies_model.createProperty(Config.PROPERTY_URL + "business"), business)
                    .addProperty(FOAF.name, companyName);
        }
        companies_csvReader.close();

        companies_model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH + "company.nt")), true), "NT");

        //University
        Model university_model = ModelFactory.createDefaultModel();

        BufferedReader university_csvReader = new BufferedReader(new FileReader(Config.AUTHOR_UNIVERSITY_PATH));
        String university_row;
        while ((university_row = university_csvReader.readLine()) != null) {

            String[] row_data = university_row.split(";");


            String universityName = row_data[2];
            String universityHomepage = row_data[3];

            if (!(universityHomepage.equals("N/A"))) {
                String universityUri = universityName.replaceAll("[^\\p{IsAlphabetic}]", "_");
                Resource currentUniversity = university_model.createResource(Config.RESOURCE_URL + universityUri)
                        .addProperty(RDF.type, university_model.getResource("http://www.gra.fo/publication/University"))
                        .addProperty(FOAF.name, universityName)
                        .addProperty(FOAF.homepage, universityHomepage);
            }

        }
        university_csvReader.close();

        university_model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH + "university.nt")), true), "NT");


        //review
        // booktitle,editor,ee,isbn,key,mdate,publisher,series,title,volume,year,location
        Model review_model = ModelFactory.createDefaultModel();
        Random rand = new Random();

        List<String> decisionList = new ArrayList<>(
                Arrays.asList("Approved", "Rejected"));
        // read the csv line by line
        BufferedReader review_csvReader = new BufferedReader(new FileReader(Config.REVIEW_PATH));
        //name,paper_key
        String review_row;
        List<String> reviewUri_list =  new ArrayList<String>();

        while ((review_row = review_csvReader.readLine()) != null) {
            String[] row_data = review_row.split(",");

            String paperKey = row_data[1].replaceAll("[^\\p{IsAlphabetic}]", "_");
            String paperUri = Config.RESOURCE_URL + paperKey;

            String name = row_data[0].replaceAll("[^\\p{IsAlphabetic}]", "_");

            String reviewUri = Config.RESOURCE_URL + name + "_" + paperKey;
            reviewUri_list.add(reviewUri);
            String personUri = Config.RESOURCE_URL + name;

            int randomIndex = rand.nextInt(decisionList.size());
            String decision = decisionList.get(randomIndex);

            Resource currentReview = review_model.createResource(reviewUri)
                    .addProperty(RDF.type, review_model.getResource("http://www.gra.fo/publication/Review"))
                    .addProperty(review_model.createProperty(Config.PROPERTY_URL + "decision"), decision)
                    .addProperty(review_model.createProperty(Config.PROPERTY_URL + "comment"), Utils.getComment(decision))
                    .addProperty(review_model.createProperty(Config.PROPERTY_URL + "about_paper"), review_model.createResource(paperUri));

        }
        review_csvReader.close();

        review_model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH + "reviews.nt")), true), "NT");


        //Person
        Model person_model = ModelFactory.createDefaultModel();

        BufferedReader person_csvReader = new BufferedReader(new FileReader(Config.AUTHOR_UNIVERSITY_PATH));
        String person_row;
        List<String> personUri_list = new ArrayList<String>();

        while ((person_row = person_csvReader.readLine()) != null) {
            String[] row_data = person_row.split(";");
            String ori_name = row_data[0];
            String[] names = row_data[0].split(" ");
            //String[] papers = row_data[1].split("\\|");

            String lastName = names[names.length - 1];
            String firstName = names[0];

            String ori_lastName = names[names.length - 1];
            String ori_firstName = names[0];

            //String personUri = names[0];
            for (String name : names) {
                if (!(name.equals(lastName) || name.equals(firstName))) {
                    firstName += " " + name;
                    //personUri += "_"+name;
                }
            }

            String reviewedPaper = row_data[4];
            String personUri = Config.RESOURCE_URL + ori_name.replaceAll("[^\\p{IsAlphabetic}]", "_");
            personUri_list.add(personUri);

            String workplaceUri = Config.RESOURCE_URL + row_data[2].replaceAll("[^\\p{IsAlphabetic}]", "_");

            Resource currentPerson = person_model.createResource(personUri)
                    .addProperty(RDF.type, person_model.getResource("http://www.gra.fo/publication/Author"))
                    .addProperty(FOAF.firstName, firstName)
                    .addProperty(FOAF.lastName, lastName)
                    .addProperty(person_model.createProperty(Config.PROPERTY_URL + "works_in"), person_model.createResource(workplaceUri));

            //link to review
            for (String reUri : reviewUri_list) {
                if (reUri.contains(ori_lastName) && reUri.contains(ori_firstName)) {
                    currentPerson.addProperty(person_model.createProperty(Config.PROPERTY_URL + "gives_review"),
                            person_model.createResource(reUri));
                }
            }

        }
        person_csvReader.close();

        //System.out.println(personUri_list.size());

        person_model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH + "person.nt")), true), "NT");


        //Paper
        Random randomGenerator = new Random();
        // cite,ee,journal,key,mdate,pages,title,volume,year,type,booktitle,crossref,author,keyword,reviewer
        Model paper_model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader paper_csvReader = new BufferedReader(new FileReader(Config.PAPER_PATH));
        String paper_row;
        List<String> keywords = Arrays.asList("Database","Cyber_Security","Maching_learning");

        while ((paper_row = paper_csvReader.readLine()) != null) {
            String[] row_data = paper_row.split(",");

            String title = row_data[6];
            String year = row_data[8];
            String keyword = row_data[13];
            String ori_name = row_data[12];

            String[] names = row_data[12].split(" ");

            String lastName = names[names.length - 1];

            String firstName = names[0];
            String personUri = names[0];
            for (String name : names) {
                if (!(name.equals(lastName) || name.equals(firstName))) {
                    firstName += " " + name;
                    personUri += "_" + name;
                }
            }

            //personUri = Config.RESOURCE_URL + personUri + lastName;
            personUri = Config.RESOURCE_URL + ori_name.replaceAll("[^\\p{IsAlphabetic}]", "_");

            // the URI of paper is name
            String paperUri = Config.RESOURCE_URL + row_data[3].replaceAll("[^\\p{IsAlphabetic}]", "_");

            Resource currentPaper = paper_model.createResource(paperUri)
                    .addProperty(paper_model.createProperty(Config.PROPERTY_URL + "written_by"), paper_model.createResource(personUri))
                    .addProperty(paper_model.createProperty(Config.PROPERTY_URL + "title"), title)
                    .addProperty(paper_model.createProperty(Config.PROPERTY_URL + "publication_year"), year);

            String paperKey = row_data[3].replaceAll("[^\\p{IsAlphabetic}]", "_");
            String[] rw_names = row_data[14].split("\\|");
            for (String rw_name : rw_names) {
                String rw_name_ = rw_name.replaceAll("[^\\p{IsAlphabetic}]", "_");
                String reviewUri = Config.RESOURCE_URL + rw_name_ + "_" + paperKey;
                currentPaper.addProperty(paper_model.createProperty(Config.PROPERTY_URL + "submitted_for"), paper_model.createResource(reviewUri));
            }


            Double randomDouble = Math.random();
            if (randomDouble < 0.25) {
                // shortPaper
                currentPaper.addProperty(RDF.type, paper_model.getResource("http://www.gra.fo/publication/Short_paper"));
                currentPaper.addProperty(paper_model.createProperty(Config.PROPERTY_URL + "short_page_size"), String.valueOf(Utils.getRandomNumberInRange(3, 5)));
            } else if (randomDouble > 0.25 && randomDouble < 0.5) {
                // demoPaper
                currentPaper.addProperty(RDF.type, paper_model.getResource("http://www.gra.fo/publication/Demo_paper"));
                String randomVideoUrl = RandomStringUtils.randomAlphanumeric(15);
                currentPaper.addProperty(paper_model.createProperty(Config.PROPERTY_URL + "demo_link"), "http://demo.com/" + randomVideoUrl);
            } else if (randomDouble > 0.5 && randomDouble < 0.75) {
                // poster
                currentPaper.addProperty(RDF.type, paper_model.getResource("http://www.gra.fo/publication/Poster"));
                String randomFormUrl = RandomStringUtils.randomAlphanumeric(15);
                currentPaper.addProperty(paper_model.createProperty(Config.PROPERTY_URL + "poster_link"), "https://www.poster.com/" + randomFormUrl);
            } else {
                // fullPaper
                currentPaper.addProperty(RDF.type, paper_model.getResource("http://www.gra.fo/publication/Full_paper"));
                currentPaper.addProperty(paper_model.createProperty(Config.PROPERTY_URL + "long_page_size"), String.valueOf(Utils.getRandomNumberInRange(10, 15)));
            }
			
			/*
            // Add keyword 
            for(String kw:keyword.split("\\|")){
				if (kw.length()>0){
					currentPaper.addProperty(paper_model.createProperty(Config.PROPERTY_URL+"contains"),paper_model.createResource(Config.RESOURCE_URL+"keyword_"+kw));
				}
			}
			*/

            // Add keyword (taking any from the title that have length > 3)
            String kw = keywords.get(Utils.getRandomNumberInRange(0, 2));
            currentPaper.addProperty(paper_model.createProperty(Config.PROPERTY_URL + "contains"), paper_model.createResource(Config.RESOURCE_URL + "keyword_" + kw.replaceAll("[^\\p{IsAlphabetic}]", "_")));
                /*
            for (String kw : title.split(" ")) {
                if (kw.length() > 3) {
                    currentPaper.addProperty(paper_model.createProperty(Config.PROPERTY_URL + "contains"), paper_model.createResource(Config.RESOURCE_URL + "keyword_" + kw.replaceAll("[^\\p{IsAlphabetic}]", "_")));
                }
            }
			*/
			

            String journal = row_data[2];
            String conference = row_data[10];
            //String jorc =  row_data[9];

            // Published In
            if (conference.length() > 1)
                currentPaper.addProperty(paper_model.createProperty(Config.PROPERTY_URL + "published_in"), paper_model.createResource(Config.RESOURCE_URL + "conf_" + conference.replaceAll("[^\\p{IsAlphabetic}]", "_")));
            if (journal.length() > 1)
                currentPaper.addProperty(paper_model.createProperty(Config.PROPERTY_URL + "published_in"), paper_model.createResource(Config.RESOURCE_URL + "journals_" + journal.replaceAll("[^\\p{IsAlphabetic}]", "_")));
        }
        paper_csvReader.close();

        paper_model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH + "paper.nt")), true), "NT");


        //conference
        Model conferenc_model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader conferenc_csvReader = new BufferedReader(new FileReader(Config.PROCEEDING_PATH));

        String conferenc_row;
        while ((conferenc_row = conferenc_csvReader.readLine()) != null) {

            String[] row_data = conferenc_row.split(";");
            String title = row_data[0];
            String publisher = row_data[6];
            String conferenceUri = Config.RESOURCE_URL + "conf_" + title.replaceAll("[^\\p{IsAlphabetic}]", "_");

            Resource currentConference = conferenc_model.createResource(conferenceUri)
                    .addProperty(RDF.type, conferenc_model.getResource("http://www.gra.fo/publication/Conference"))
                    .addProperty(conferenc_model.createProperty(Config.PROPERTY_URL + "confName"), title)
                    .addProperty(conferenc_model.createProperty(Config.PROPERTY_URL + "confPublisher"), publisher)
                    .addProperty(conferenc_model.createProperty(Config.PROPERTY_URL + "handled_byc"), conferenc_model.createResource(personUri_list.get(Utils.getRandomNumberInRange(0, personUri_list.size() - 1))));

        }
        conferenc_csvReader.close();

        conferenc_model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH + "conference.nt")), true), "NT");


        //journal
        // title, volume, year
        Model journal_model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader journal_csvReader = new BufferedReader(new FileReader(Config.JOURNAL_PATH));
        String journal_row;
        while ((journal_row = journal_csvReader.readLine()) != null) {
            String[] row_data = journal_row.split(",");
            String journal = row_data[0];
            //String year = row_data[2];
            String publisher = row_data[3];
            String journalUri = Config.RESOURCE_URL + "journals_" + journal.replaceAll("[^\\p{IsAlphabetic}]", "_");

            Resource currentJournalVolume = journal_model.createResource(journalUri)
                    .addProperty(RDF.type, journal_model.getResource("http://www.gra.fo/publication/Journal"))
                    .addProperty(journal_model.createProperty(Config.PROPERTY_URL + "journalName"), journal)
                    .addProperty(journal_model.createProperty(Config.PROPERTY_URL + "handled_byj"), journal_model.createResource(personUri_list.get(Utils.getRandomNumberInRange(0, personUri_list.size() - 1))))
                    .addProperty(journal_model.createProperty(Config.PROPERTY_URL + "journalPublisher"), publisher);
        }
        journal_csvReader.close();

        journal_model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH + "journal.nt")), true), "NT");


        //keyword

        Model keyword_model = ModelFactory.createDefaultModel();
		
		for (int i = 0; i < 3; i++) {
			String keywordUri = Config.RESOURCE_URL + "keyword_" + keywords.get(i);
			Resource currentTitle = keyword_model.createResource(keywordUri)
					.addProperty(RDF.type, keyword_model.getResource("http://www.gra.fo/publication/"+keywords.get(i)))
					.addProperty(keyword_model.createProperty(Config.PROPERTY_URL + "keyword_value"), keywords.get(i));
		}
		/*
        BufferedReader keyword_csvReader = new BufferedReader(new FileReader(Config.PAPER_PATH));
        String keyword_row;
        while ((keyword_row = keyword_csvReader.readLine()) != null) {
            String[] row_data = keyword_row.split(",");

            String title = row_data[6];
            for (String kw : title.split(" ")) {
                if (kw.length() > 3) {
                    String keywordUri = Config.RESOURCE_URL + "keyword_" + kw.replaceAll("[^\\p{IsAlphabetic}]", "_");
                    Resource currentTitle = keyword_model.createResource(keywordUri)
                            .addProperty(RDF.type, keyword_model.getResource("http://www.gra.fo/publication/Keyword"))
                            .addProperty(keyword_model.createProperty(Config.PROPERTY_URL + "keyword"), kw);
                }
            }

        }
        keyword_csvReader.close();
		*/
        keyword_model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH + "keyword.nt")), true), "NT");

    }
}