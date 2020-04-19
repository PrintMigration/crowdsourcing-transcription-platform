package mainApplication.customSerializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import mainApplication.entities.Document;

public class DocumentSerializer extends StdSerializer<Document>{
	static final long serialVersionUID = 1L;

	public DocumentSerializer() {
	        this(null);
    }
 
    public DocumentSerializer(Class<Document> t) {
        super(t);
    }
 
    @Override
    public void serialize(Document doc, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
        generator.writeObject(doc.getDocumentID());
    }
}
