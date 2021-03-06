package br.com.radio.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JSONOnlyDateSerializer extends JsonSerializer<Date> {

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException, JsonProcessingException {
		
		if ( date != null ){
			
			String dateAsText = ""; 
			try
			{
				dateAsText = sdf.format(date);
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
			
			jsonGenerator.writeString(dateAsText);
		}
		
	}

}
