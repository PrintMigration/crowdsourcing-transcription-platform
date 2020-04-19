package mainApplication.customSerializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import mainApplication.entities.Document;

public class DocumentListSerializer extends StdSerializer<List<Document>>{
	static final long serialVersionUID = 1L;

	public DocumentListSerializer() {
	        this(null);
    }
 
    public DocumentListSerializer(Class<List<Document>> t) {
        super(t);
    }
 
    @Override
    public void serialize(List<Document> docs, JsonGenerator generator, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        List<Integer> ids = new ArrayList<>();
        for (Document doc : docs) {
            ids.add(doc.getDocumentID());
        }
        generator.writeObject(ids);
    }
}
