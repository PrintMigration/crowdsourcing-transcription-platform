package mainApplication.customSerializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import mainApplication.entities.PlaceRoleDocument;

public class PlaceRoleDocumentSerializer extends StdSerializer<List<PlaceRoleDocument>>{
	static final long serialVersionUID = 1L;

	public PlaceRoleDocumentSerializer() {
        this(null);
    }
 
    public PlaceRoleDocumentSerializer(Class<List<PlaceRoleDocument>> t) {
        super(t);
    }
 
    @Override
    public void serialize(List<PlaceRoleDocument> placerds, JsonGenerator generator, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        List<Integer> ids = new ArrayList<>();
        for (PlaceRoleDocument placerd : placerds) {
            ids.add(placerd.getPp2dID());
        }
        generator.writeObject(ids);
    }
}
