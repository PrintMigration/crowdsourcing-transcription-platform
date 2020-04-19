package mainApplication.customSerializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import mainApplication.entities.Person;

public class SinglePersonSerializer extends StdSerializer<Person>{
	static final long serialVersionUID = 1L;

	public SinglePersonSerializer() {
        this(null);
    }
 
    public SinglePersonSerializer(Class<Person> t) {
        super(t);
    }
 
    @Override
    public void serialize(Person p, JsonGenerator generator, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        generator.writeObject(p.getPersonID());
    }
}
