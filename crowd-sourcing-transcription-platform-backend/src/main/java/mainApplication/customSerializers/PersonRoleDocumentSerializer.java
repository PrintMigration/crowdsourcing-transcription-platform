package mainApplication.customSerializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import mainApplication.entities.PersonRoleDocument;

public class PersonRoleDocumentSerializer extends StdSerializer<List<PersonRoleDocument>>{
	static final long serialVersionUID = 1L;

	public PersonRoleDocumentSerializer() {
        this(null);
    }
 
    public PersonRoleDocumentSerializer(Class<List<PersonRoleDocument>> t) {
        super(t);
    }
 
    @Override
    public void serialize(List<PersonRoleDocument> prds, JsonGenerator generator, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        List<Integer> ids = new ArrayList<>();
        for (PersonRoleDocument prd : prds) {
            ids.add(prd.getP2dID());
        }
        generator.writeObject(ids);
    }
}
