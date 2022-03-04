import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import java.util.*;

public class SampleClient {

    public static void main(String[] theArgs) {

        // Create a FHIR client
        FhirContext fhirContext = FhirContext.forR4();
        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        client.registerInterceptor(new LoggingInterceptor(false));

        // Search for Patient resources
        Bundle response = client
                .search()
                .forResource("Patient")
                .where(Patient.FAMILY.matches().value("SMITH"))
                .returnBundle(Bundle.class)
                .execute();
        int i = 0;
        Set<User> users = new HashSet<>();
        for(i = 0; i<response.getEntry().size(); i++) {
        	if(response.getEntry().get(i).getResource().getChildByName("birthDate").getValues().size() != 0) {
	        	String s = response.getEntry().get(i).getResource().getChildByName("name").getValues().get(0).getNamedProperty("given").getValues().get(0).toString();
	        	String dob = response.getEntry().get(i).getResource().getChildByName("birthDate").getValues().get(0).toString();
	        	String[] parts = s.split("\"");
	        	String fname = parts[0];
	        	fname = fname.substring(0, 1).toUpperCase() + fname.substring(1);
	        	String lname = response.getEntry().get(i).getResource().getChildByName("name").getValues().get(0).getNamedProperty("family").getValues().get(0).toString();
	        	lname = lname.substring(0, 1).toUpperCase() + lname.substring(1);
	        	User u = new User(fname, lname, dob);
	        	users.add(u);
        	}
        	else
        	{
        		String s = response.getEntry().get(i).getResource().getChildByName("name").getValues().get(0).getNamedProperty("given").getValues().get(0).toString();
	        	String[] parts = s.split("\"");
	        	String fname = parts[0];
	        	fname = fname.substring(0, 1).toUpperCase() + fname.substring(1);
	        	String lname = response.getEntry().get(i).getResource().getChildByName("name").getValues().get(0).getNamedProperty("family").getValues().get(0).toString();
	        	lname = lname.substring(0, 1).toUpperCase() + lname.substring(1);
	        	String dob = "null";
	        	User u = new User(fname, lname, dob);
	        	users.add(u);
        	}        	
        }
        System.out.println("Sorted First Name");
    	UserComparator first = new UserComparator(UserComparator.FIRSTNAME);
    	ArrayList<User> sortedFirst = new ArrayList(users);
    	Collections.sort(sortedFirst, first);
    	for(User a : sortedFirst)
    	System.out.println(a);
        
    }

}
class User
{
	public String firstname;
	public String lastname;
	public String dob;

	public User(String firstname, String lastname, String dob)
	{
		this.firstname = firstname;
		this.lastname = lastname;
		this.dob = dob;
	}

	public String toString()
	{
		return firstname + " " + lastname + " " + dob;
	}
}

class UserComparator implements Comparator
{
	public static final User test = new User("", "", "");
	public static final int FIRSTNAME = 0;
	public static final int LASTNAME = 1;
	public static final int AGE = 2;
	public int selction;

	public UserComparator(int selection)
	{
		this.selction = selection;
	}
	public int compare(Object one, Object two)
	{
		if(!one.getClass().isInstance(test)) return 0;
		if(!two.getClass().isInstance(test)) return 0;
		User a = (User)one;
		User b = (User)two;
		switch(selction)
		{
			case FIRSTNAME: return a.firstname.compareTo(b.firstname);
			case LASTNAME: return a.lastname.compareTo(b.lastname);
			default: return a.dob.compareTo(b.dob);
		}
	}
}
