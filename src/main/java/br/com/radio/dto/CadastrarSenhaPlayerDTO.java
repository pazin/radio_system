package br.com.radio.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import br.com.radio.validation.PasswordMatches;

@PasswordMatches
public class CadastrarSenhaPlayerDTO implements IPasswordMatch {
	
	@NotNull
    @NotEmpty
    private String password;
	
	@NotNull
    @NotEmpty
    private String matchingPassword;

	public String getPassword()
	{
		return password;
	}

	public void setPassword( String password )
	{
		this.password = password;
	}

	public String getMatchingPassword()
	{
		return matchingPassword;
	}

	public void setMatchingPassword( String matchingPassword )
	{
		this.matchingPassword = matchingPassword;
	}
	
}
