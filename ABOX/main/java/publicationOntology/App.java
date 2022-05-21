package publicationOntology;

import publicationOntology.abox.Creator;

public class App 
{
    public static void main(String[] args) throws Exception {
            Creator.createCompany();
            Creator.createConference();
            //Creator.createEdition();
            Creator.createJournal();
            //Creator.createVolume();
            Creator.createKeyword();
            Creator.createPaper();
            Creator.createPerson();
            //Creator.createProceedings();
            Creator.createReview();
            Creator.createUniversity();
        
    }
}
