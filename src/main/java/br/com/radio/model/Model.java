package br.com.radio.model;

import java.io.Serializable;

public interface Model<ID extends Serializable> extends Serializable {

	ID getId();
	
	void setId( ID id );
}
