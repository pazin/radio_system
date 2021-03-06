package br.com.radio.config;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerConfig {
	
	private static final Logger logger = Logger.getLogger(ControllerConfig.class);

	@ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handle( HttpMessageNotReadableException e )
	{
		logger.warn( "Returning HTTP 400 Bad Request", e );
		throw e;
	}
	
}
