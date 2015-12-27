package br.com.radio.web;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.radio.enumeration.UsuarioTipo;
import br.com.radio.json.JSONBootstrapGridWrapper;
import br.com.radio.json.JSONListWrapper;
import br.com.radio.model.Ambiente;
import br.com.radio.model.Conversa;
import br.com.radio.model.Mensagem;
import br.com.radio.model.Usuario;
import br.com.radio.repository.ConversaRepository;
import br.com.radio.repository.MensagemRepository;
import br.com.radio.repository.UsuarioRepository;
import br.com.radio.service.UsuarioService;

@Controller
public class MensagemController extends AbstractController {
	
	
	@Autowired
	private ConversaRepository conversaRepo;
	
	@Autowired
	private MensagemRepository mensagemRepo;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepo;


	@RequestMapping(value="/conversas/view", method=RequestMethod.GET)
	@PreAuthorize("hasAuthority('MENSAGENS')")
	public String conversas( ModelMap model )
	{
		return "gerenciador/conversas";
	}
	
	
	
	@RequestMapping( value = { "/conversas", "/api/conversas" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	public @ResponseBody JSONBootstrapGridWrapper<Conversa> getConversas(  
																 @RequestParam(value="pageNumber", required=false) Integer pageNumber, 
																 @RequestParam(value="limit", required=false) Integer limit,
																 Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente() == null )
			return null;
		
		Pageable pageable = getPageable( pageNumber, limit, "asc", "idConversa" );
		
		Page<Conversa> conversaPage = conversaRepo.findByClienteAndAtivo( pageable, usuario.getCliente(), true );
		
		List<Conversa> conversas = conversaPage.getContent();
		
		conversas.forEach( c -> {
			c.getConversaView().put( "ambiente.nome", c.getAmbiente().getNome() );
		});
		
		JSONBootstrapGridWrapper<Conversa> jsonList = new JSONBootstrapGridWrapper<Conversa>(conversas, conversaPage.getTotalElements() );

		return jsonList;
	}
	
	
	@RequestMapping( value = { "/conversas", "/api/conversas" }, method = { RequestMethod.POST }, consumes = "application/json", produces = APPLICATION_JSON_CHARSET_UTF_8 )
	public @ResponseBody String saveAmbiente( @RequestBody @Valid Conversa conversaDTO, BindingResult result )
	{
		String jsonResult = null;
		
		if ( result.hasErrors() ){
			
			jsonResult = writeErrorsAsJSONErroMessage(result);	
		}
		else
		{
			try
			{
				if ( conversaDTO != null )
					System.out.println("opa");
				
				
				
				jsonResult = writeOkResponse();
			}
			catch ( Exception e )
			{
				e.printStackTrace();
				jsonResult = writeSingleErrorAsJSONErroMessage( "alertArea", e.getMessage() );
			}
		}

		return jsonResult;
	}
	
	
	
	
	@RequestMapping( value = { "/conversas/usuarios", "/api/conversas/usuarios" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	public @ResponseBody JSONListWrapper<Usuario> getUsuarios( @RequestParam(value="pageNumber", required=false) Integer pageNumber, 
																 @RequestParam(value="limit", required=false) Integer limit,
																 @RequestParam(value="all", required=false) Boolean all,
																 @RequestParam(value="sort", required=false) String sort,
																 Principal principal )
	{
		// Pegando a empresa pelo usuário logado
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente() == null )
			return null;
		
		Pageable pageable = getPageable( pageNumber, limit, "asc", sort );
		
		
		Page<Usuario> usuarioPage = null;
		
		if ( all != null && all )
			usuarioPage = usuarioRepo.findByCliente( pageable, usuario.getCliente() );
		else
			usuarioPage = usuarioRepo.findByClienteAndUsuarioTipo( pageable, usuario.getCliente(), UsuarioTipo.GERENCIADOR );

		List<Usuario> usuariosList = usuarioPage.getContent();
		
		usuariosList.forEach( u -> {

			if ( u.getAmbiente() != null )
				u.getUsuarioView().put( "idAmbiente", u.getAmbiente().getIdAmbiente().toString() );
		});
		
		
		JSONListWrapper<Usuario> jsonList = new JSONListWrapper<Usuario>(usuarioPage.getContent(), usuarioPage.getTotalElements() );

		return jsonList;
	}

	
	
	@RequestMapping( value = { "/conversas/{idConversa}/mensagens", "/api/conversas/{idConversa}/mensagens" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	public @ResponseBody JSONBootstrapGridWrapper<Mensagem> getMensagens( @PathVariable Long idConversa, Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente() == null )
			return null;
		
		Conversa conversa = conversaRepo.findOne( idConversa );
		
		if ( conversa == null )
			throw new RuntimeException( "Conversação não encontrada" );

		boolean mesmaCliente = conversa.getAmbiente().getCliente().getIdCliente().equals( usuario.getCliente().getIdCliente() );
		
		if ( conversa.getAmbiente() == null || !mesmaCliente )
			throw new RuntimeException( "Problema com a Cliente" );

		List<Mensagem> mensagens = conversa.getMensagens();
		
		mensagens.stream().forEach( m -> {
			if ( m.getUsuario().getIdUsuario().equals( usuario.getIdUsuario() ) )
				m.getMensagemView().put( "htmlclass", "self" );
			else
				m.getMensagemView().put( "htmlclass", "other" );
		});
		
		JSONBootstrapGridWrapper<Mensagem> jsonList = new JSONBootstrapGridWrapper<Mensagem>(mensagens, mensagens.size() );

		return jsonList;
	}
	
	
}
