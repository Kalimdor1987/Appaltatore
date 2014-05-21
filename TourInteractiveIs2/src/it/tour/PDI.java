package it.tour;
public class PDI {
    private String  name;
    private String  visto;

    public PDI ( String name, String visit) {
            super();
            this.name = name;
            this.visto = visit;
            
    }

    public String getName () {
            return name;
    }

    public String getSurname () {
            return visto;
    }

   public void setName(String name)
   {
	   this.name=name;
   }
   
   public void setVisit(String visit)
   {
	   this.visto=visit;
   }
   
    
}
