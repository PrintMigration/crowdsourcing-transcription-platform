package mainApplication.customSerializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import mainApplication.entities.DocumentEdit;

public class DocumentEditSerializer extends StdSerializer<List<DocumentEdit>>{
	static final long serialVersionUID = 1L;

	public DocumentEditSerializer() {
	        this(null);
    }
 
    public DocumentEditSerializer(Class<List<DocumentEdit>> t) {
        super(t);
    }
 
    @Override
    public void serialize(List<DocumentEdit> docs, JsonGenerator generator, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        List<Integer> ids = new ArrayList<>();
        for (DocumentEdit doc : docs) {
            ids.add(doc.getDocumentEditID());
        }
        generator.writeObject(ids);
    }
}
