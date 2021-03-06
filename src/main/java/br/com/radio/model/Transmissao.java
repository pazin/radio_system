package br.com.radio.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.radio.enumeration.StatusPlayback;
import br.com.radio.json.JSONDateDeserializer;
import br.com.radio.json.JSONDateTimeSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Casa registro dessa tabela representa um arquivo de midia que deverá ser tocado em ordem pelo player.
 * 
 * Pode ser feito o download antecipadamente ou pode ser tocado ao vivo
 * 
 * @author pazin
 *
 */
@Entity
@Table(name="transmissao")
public class Transmissao implements Serializable {

	private static final long serialVersionUID = -66420223362665206L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column( name = "id_transmissao", nullable = false )
	private Long idTransmissao;
	
	@JsonIgnore
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_ambiente")
	private Ambiente ambiente;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_programacao" )
	private Programacao programacao;
	
	@OneToOne
	@JoinColumn(name="id_midia" )
	private Midia midia;
	
	@Column( name = "posicaoplay", nullable = false )
	private Double posicaoplay;
	
	@JsonDeserialize(using=JSONDateDeserializer.class)
	@JsonSerialize(using=JSONDateTimeSerializer.class)
	@Temporal( TemporalType.DATE )
	@Column( name = "diaplay" )
	private Date diaPlay;        // Essa data não vai guardar a HORA... é só pra saber o dia/mes/ano

	@JsonDeserialize(using=JSONDateDeserializer.class)
	@JsonSerialize(using=JSONDateTimeSerializer.class)
	@Temporal( TemporalType.TIMESTAMP )
	@Column( name = "dataprevisaoplay" )
	private Date dataPrevisaoPlay;

	// * Duração da midia em segundos... 
	@Column( name = "duracao")
	private Integer duracao;
	
	@Column( name = "downloadcompleto")
	private Boolean downloadcompleto;
	
	@Enumerated(EnumType.STRING)
	@Column(name="statusplayback")
	private StatusPlayback statusPlayback;
	
	@Column( name= "link" )
	private String link;
	
	@Column( name= "linkativo" )
	private Boolean linkativo;
	
	
	// Não é preenchida quando a música sofre skip...
	@JsonDeserialize(using=JSONDateDeserializer.class)
	@JsonSerialize(using=JSONDateTimeSerializer.class)
	@Temporal( TemporalType.TIMESTAMP )
	@Column( name = "datafinishplay" )
	private Date dataFinishPlay;
	
	@JsonIgnore
	@Temporal( TemporalType.TIMESTAMP )
	@Column( name = "datacriacao" )
	private Date dataCriacao;

	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_categoria")
	private Categoria categoria;
	
	@JsonIgnore
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_eventohorario")
	private EventoHorario eventoHorario;
	
	

	public Long getIdTransmissao()
	{
		return idTransmissao;
	}


	public void setIdTransmissao( Long idTransmissao )
	{
		this.idTransmissao = idTransmissao;
	}


	public Ambiente getAmbiente()
	{
		return ambiente;
	}


	public void setAmbiente( Ambiente ambiente )
	{
		this.ambiente = ambiente;
	}


	public Programacao getProgramacao()
	{
		return programacao;
	}


	public void setProgramacao( Programacao programacao )
	{
		this.programacao = programacao;
	}


	public Midia getMidia()
	{
		return midia;
	}


	public void setMidia( Midia midia )
	{
		this.midia = midia;
	}


	public Date getDataPrevisaoPlay()
	{
		return dataPrevisaoPlay;
	}


	public void setDataPrevisaoPlay( Date dataPrevisaoPlay )
	{
		this.dataPrevisaoPlay = dataPrevisaoPlay;
	}


	public Integer getDuracao()
	{
		return duracao;
	}


	public void setDuracao( Integer duracao )
	{
		this.duracao = duracao;
	}


	public Boolean getDownloadcompleto()
	{
		return downloadcompleto;
	}


	public void setDownloadcompleto( Boolean downloadcompleto )
	{
		this.downloadcompleto = downloadcompleto;
	}


	public StatusPlayback getStatusPlayback()
	{
		return statusPlayback;
	}


	public void setStatusPlayback( StatusPlayback statusPlayback )
	{
		this.statusPlayback = statusPlayback;
	}


	public String getLink()
	{
		return link;
	}


	public void setLink( String link )
	{
		this.link = link;
	}


	public Boolean getLinkativo()
	{
		return linkativo;
	}


	public void setLinkativo( Boolean linkativo )
	{
		this.linkativo = linkativo;
	}


	public Date getDataFinishPlay()
	{
		return dataFinishPlay;
	}


	public void setDataFinishPlay( Date dataFinishPlay )
	{
		this.dataFinishPlay = dataFinishPlay;
	}


	public Date getDataCriacao()
	{
		return dataCriacao;
	}


	public void setDataCriacao( Date dataCriacao )
	{
		this.dataCriacao = dataCriacao;
	}


	public Transmissao()
	{
		super();
		this.dataCriacao = new Date();
	}


	public Transmissao( Long idTransmissao )
	{
		super();
		this.idTransmissao = idTransmissao;
		this.dataCriacao = new Date();
	}


	public Double getPosicaoplay()
	{
		return posicaoplay;
	}


	public void setPosicaoplay( Double posicaoplay )
	{
		this.posicaoplay = posicaoplay;
	}


	public Date getDiaPlay()
	{
		return diaPlay;
	}


	public void setDiaPlay( Date diaPlay )
	{
		this.diaPlay = diaPlay;
	}


	public Categoria getCategoria()
	{
		return categoria;
	}


	public void setCategoria( Categoria categoria )
	{
		this.categoria = categoria;
	}


	public EventoHorario getEventoHorario()
	{
		return eventoHorario;
	}


	public void setEventoHorario( EventoHorario eventoHorario )
	{
		this.eventoHorario = eventoHorario;
	}


	@Override
	public String toString()
	{
		return String.format( "Transmissao [idTransmissao=%s, posicaoplay=%s, dataPrevisaoPlay=%s, eventoHorario=%s]", idTransmissao, posicaoplay, dataPrevisaoPlay, eventoHorario );
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( idTransmissao == null ) ? 0 : idTransmissao.hashCode() );
		return result;
	}


	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		Transmissao other = (Transmissao) obj;
		if ( idTransmissao == null )
		{
			if ( other.idTransmissao != null )
				return false;
		}
		else if ( !idTransmissao.equals( other.idTransmissao ) )
			return false;
		return true;
	}
	
	
	
}
