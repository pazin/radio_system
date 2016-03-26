package br.com.radio.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.radio.model.Cliente;
import br.com.radio.model.CondicaoComercial;
import br.com.radio.model.Telefone;
import br.com.radio.repository.ClienteRepository;
import br.com.radio.repository.CondicaoComercialRepository;
import br.com.radio.repository.TelefoneRepository;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepo;
	
	@Autowired
	private TelefoneRepository telefoneRepo;

	@Autowired
	private CondicaoComercialRepository ccRepo;



	@Transactional
	public Cliente saveCliente( Cliente clienteVO )
	{
		if ( clienteVO.getAtivo() == null )
			clienteVO.setAtivo( false );
 
		if ( StringUtils.isNotBlank( clienteVO.getCnpj() ) )
			clienteVO.setCnpj( clienteVO.getCnpj().replaceAll("\\D+","") );
		
		List<Telefone> telefones = clienteVO.getTelefones();
		
		boolean clienteNovo = clienteVO.getIdCliente() == null;
		
		if ( clienteNovo )
		{
			Cliente clienteExistente = clienteRepo.findByCnpj( clienteVO.getCnpj() );
			
			if ( clienteExistente != null )
				throw new RuntimeException( "Cliente já existe no banco de dados." );
		}
		
		clienteRepo.save( clienteVO );

		if ( clienteNovo )
		{
			List<CondicaoComercial> condicoesComerciais = ccRepo.findByCliente( clienteVO );
			
			if ( condicoesComerciais != null && condicoesComerciais.size() == 0 )
			{
				// Buscando as condicoes comerciais default
				List<CondicaoComercial> condicoesDefault = ccRepo.findByCliente( null );
				
				for (CondicaoComercial ccDefault : condicoesDefault )
				{
					CondicaoComercial ccNova = new CondicaoComercial();
					BeanUtils.copyProperties( ccDefault, ccNova );
					ccNova.setCliente( clienteVO );
				}
			}
		}
		
		if ( clienteVO.getIdCliente() != null && clienteVO.getIdCliente() > 0 )
			telefoneRepo.deleteByCliente( clienteVO );
		
		if ( telefones != null )
		{
			for ( Telefone tel : telefones )
			{
				if ( tel != null ){
					tel.setCliente( clienteVO );
					
					telefoneRepo.save( tel );
				}
			}
		}

		return clienteVO;
	}
	

}
