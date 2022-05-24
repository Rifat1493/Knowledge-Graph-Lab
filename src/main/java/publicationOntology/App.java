package publicationOntology;

import publicationOntology.abox.Creator;
import publicationOntology.abox.Tbox;

public class App 
{
    public static void main(String[] args) throws Exception {
			Creator.createALL();
        if (args[0].equals("abox")) {
            Creator.createALL();
        } else if (args[0].equals("tbox")){
            Tbox.createRDFS();
        }
    }
}
