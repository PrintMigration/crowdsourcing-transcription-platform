package mainApplication.customSerializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import mainApplication.entities.OrganizationRoleDocument;

public class OrganizationRoleDocumentSerializer extends StdSerializer<List<OrganizationRoleDocument>>{
	static final long serialVersionUID = 1L;

	public OrganizationRoleDocumentSerializer() {
        this(null);
    }
 
    public OrganizationRoleDocumentSerializer(Class<List<OrganizationRoleDocument>> t) {
        super(t);
    }
 
    @Override
    public void serialize(List<OrganizationRoleDocument> ords, JsonGenerator generator, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        List<Integer> ids = new ArrayList<>();
        for (OrganizationRoleDocument ord : ords) {
            ids.add(ord.getO2dID());
        }
        generator.writeObject(ids);
    }
}
