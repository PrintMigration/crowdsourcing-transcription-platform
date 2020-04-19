package mainApplication.customSerializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import mainApplication.entities.PersonReligion;

public class PersonReligionSerializer extends StdSerializer<List<PersonReligion>>{
	static final long serialVersionUID = 1L;

	public PersonReligionSerializer() {
	        this(null);
    }
 
    public PersonReligionSerializer(Class<List<PersonReligion>> t) {
        super(t);
    }
 
    @Override
    public void serialize(List<PersonReligion> personReligion, JsonGenerator generator, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        List<Integer> ids = new ArrayList<>();
        for (PersonReligion p2rel : personReligion) {
            ids.add(p2rel.getP2relID());
        }
        generator.writeObject(ids);
    }
}
