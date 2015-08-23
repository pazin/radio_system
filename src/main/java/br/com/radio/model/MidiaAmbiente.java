package br.com.radio.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.radio.json.JSONDateDeserializer;
import br.com.radio.json.JSONDateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;



/**
 * Tabela de ligação 
 * 
 * Midia x Ambiente = MIA
 * 
 * @author pazin
 *
 */
@Entity
@Table(name="midia_ambiente")
public class MidiaAmbiente implements Serializable {
	
	private static final long serialVersionUID = -7404421157947787150L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column( name = "id_midiaamb_mia", nullable = false )
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="id_ambiente_amb")
	private Ambiente ambiente;
	
	@ManyToOne
	@JoinColumn(name="id_midia_mid")
	private Midia midia;
	
	@JsonDeserialize(using=JSONDateDeserializer.class)
	@JsonSerialize(using=JSONDateSerializer.class)
	@Temporal( TemporalType.TIMESTAMP )
	@Column( name = "dt_associacao_mia" )
	private Date dataAssociacao;
	

	// talvez guardar o usuário que associou?



	public Long getId()
	{
		return id;
	}

	public void setId( Long id )
	{
		this.id = id;
	}

	public Ambiente getAmbiente()
	{
		return ambiente;
	}

	public void setAmbiente( Ambiente ambiente )
	{
		this.ambiente = ambiente;
	}

	public Midia getMidia()
	{
		return midia;
	}

	public void setMidia( Midia midia )
	{
		this.midia = midia;
	}

	public Date getDataAssociacao()
	{
		return dataAssociacao;
	}

	public void setDataAssociacao( Date dataAssociacao )
	{
		this.dataAssociacao = dataAssociacao;
	}
	
	

	

}
