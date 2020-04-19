package mainApplication.customSerializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import mainApplication.entities.OrganizationReligion;

public class OrganizationReligionSerializer extends StdSerializer<List<OrganizationReligion>>{
	static final long serialVersionUID = 1L;

	public OrganizationReligionSerializer() {
	        this(null);
    }
 
    public OrganizationReligionSerializer(Class<List<OrganizationReligion>> t) {
        super(t);
    }
 
    @Override
    public void serialize(List<OrganizationReligion> ors, JsonGenerator generator, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        List<Integer> ids = new ArrayList<>();
        for (OrganizationReligion or : ors) {
            ids.add(or.getO2rID());
        }
        generator.writeObject(ids);
    }
}
