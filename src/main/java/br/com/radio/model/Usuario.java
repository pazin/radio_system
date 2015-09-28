package br.com.radio.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.radio.json.JSONDateSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@Entity
@Table(name="usuario")
public class Usuario implements Serializable {

	private static final long serialVersionUID = -7625966973767359067L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column( name = "id_usuario", nullable = false )
	private Long idUsuario;
	
	@JsonIgnore
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn( name="id_empresa", nullable=false )
	private Empresa empresa;
	
	@NotNull( message = "O nome do usuário é de preenchimento obrigatório" )
	@Column( name = "nome", nullable = false, columnDefinition = "TEXT" )
	private String nome;
	
	@NotNull( message = "O email do usuário é de preenchimento obrigatório" )
	@Column( name = "email", nullable = false, length = 120 )
	private String email;
	
	@NotNull( message = "O nome de login é de preenchimento obrigatório" )
	@Column( name = "login", nullable = false, length = 40, unique = true )
	private String login;
	
	@JsonIgnore
	@NotNull( message = "A senha é de preenchimento obrigatório" )
	@Column( name = "password", nullable = false, length = 200 )
	private String password;
	
	@JsonSerialize(using=JSONDateSerializer.class)
	@NotNull( message = "A data de criação do usuário é de preenchimento obrigatório" )	
	@Temporal( TemporalType.TIMESTAMP )
	@Column( name = "datacriacao", nullable = false )
	private Date dataCriacao;
	
	@JsonSerialize(using=JSONDateSerializer.class)
	@Temporal( TemporalType.TIMESTAMP )
	@Column( name = "dataalteracao" )
	private Date dataAlteracao;
	
	@NotNull
	@Column( name = "ativo" )
	private Boolean ativo;

	@ManyToMany(fetch=FetchType.EAGER)
	   @JoinTable(name="usuario_perfil", joinColumns = { 
	        @JoinColumn(name="id_usuario", nullable=false, updatable=false) }, inverseJoinColumns = { 
	        @JoinColumn(name="id_perfil", nullable=false, updatable=false) })
	private List<Perfil> perfis;

	
	public Usuario()
	{
		super();
		this.dataCriacao = new Date();
	}

	public Long getIdUsuario()
	{
		return idUsuario;
	}

	public void setIdUsuario( Long idUsuario )
	{
		this.idUsuario = idUsuario;
	}

	public String getNome()
	{
		return nome;
	}

	public void setNome( String nome )
	{
		this.nome = nome;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail( String email )
	{
		this.email = email;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin( String login )
	{
		this.login = login;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword( String password )
	{
		this.password = password;
	}

	public Date getDataCriacao()
	{
		return dataCriacao;
	}

	public void setDataCriacao( Date dataCriacao )
	{
		this.dataCriacao = dataCriacao;
	}

	public Date getDataAlteracao()
	{
		return dataAlteracao;
	}

	public void setDataAlteracao( Date dataAlteracao )
	{
		this.dataAlteracao = dataAlteracao;
	}

	public Boolean getAtivo()
	{
		return ativo;
	}

	public void setAtivo( Boolean ativo )
	{
		this.ativo = ativo;
	}

	public Empresa getEmpresa()
	{
		return empresa;
	}

	public void setEmpresa( Empresa empresa )
	{
		this.empresa = empresa;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( idUsuario == null ) ? 0 : idUsuario.hashCode() );
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		Usuario other = (Usuario) obj;
		if ( idUsuario == null )
		{
			if ( other.idUsuario != null )
				return false;
		}
		else if ( !idUsuario.equals( other.idUsuario ) )
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return String.format( "Usuario [idUsuario=%s, empresa=%s, nome=%s, email=%s, login=%s, password=%s, dataCriacao=%s]", idUsuario, empresa, nome, email, login, password, dataCriacao );
	}

	public List<Perfil> getPerfis()
	{
		return perfis;
	}

	public void setPerfis( List<Perfil> perfis )
	{
		this.perfis = perfis;
	}
	
	
	

}