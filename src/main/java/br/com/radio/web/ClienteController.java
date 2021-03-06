package br.com.radio.web;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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

import br.com.radio.dto.cliente.ClienteRelatorioDTO;
import br.com.radio.dto.cliente.ClienteResumoFinanceiroDTO;
import br.com.radio.enumeration.UsuarioTipo;
import br.com.radio.json.JSONListWrapper;
import br.com.radio.json.JSONListWrapper;
import br.com.radio.model.Ambiente;
import br.com.radio.model.Cliente;
import br.com.radio.model.CondicaoComercial;
import br.com.radio.model.TipoTaxa;
import br.com.radio.model.Titulo;
import br.com.radio.model.Usuario;
import br.com.radio.repository.ClienteRepository;
import br.com.radio.repository.CondicaoComercialRepository;
import br.com.radio.repository.TipoTaxaRepository;
import br.com.radio.repository.TituloRepository;
import br.com.radio.service.ClienteService;
import br.com.radio.service.UsuarioService;
import br.com.radio.util.UtilsDates;
import br.com.radio.util.UtilsStr;


@Controller
public class ClienteController extends AbstractController {
	
	private final Logger logger = Logger.getLogger( ClienteController.class );

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private ClienteRepository clienteRepo;
	
	@Autowired
	private CondicaoComercialRepository ccRepo;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private TituloRepository tituloRepo;
	
	@Autowired
	private TipoTaxaRepository tipoTaxaRepo;
	

	@Override
	protected Logger getLogger()
	{
		return this.logger;
	}

	@RequestMapping(value={ "/admin/clientes/new" }, method=RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADM_SISTEMA')")
	public String novoClienteAdmin( ModelMap model, Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente() == null )
			return "HTTPerror/404";
		
		model.addAttribute( "urlVoltarCadastro", "/admin/clientes/searches" );
		model.addAttribute( "urlVoltarPainel", "/admin/painel" );
		model.addAttribute( "nomePainel", "Painel de Admin");
		model.addAttribute( "urlInserirTitulo", "/admin/titulos/new" );
		model.addAttribute( "isAdmin", true);

		return "cliente/editar-cliente";
	}

	@RequestMapping(value={ "/clientes/new" }, method=RequestMethod.GET)
	@PreAuthorize("hasAuthority('DADOS_CLIENTE')")
	public String novoCliente( ModelMap model, Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente() == null )
			return "HTTPerror/404";

		model.addAttribute( "urlVoltarPainel", "/principal" );
		model.addAttribute( "nomePainel", "Painel Gerencial" );
		model.addAttribute( "urlInserirTitulo", "/titulos/new" );
		
		return "cliente/editar-cliente";
	}


	@RequestMapping(value={ "/admin/clientes/{idCliente}/view" }, method=RequestMethod.GET)
	@PreAuthorize("hasAuthority('DADOS_CLIENTE')")
	public String editarClienteAdmin( @PathVariable Long idCliente, ModelMap model, Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente() == null )
			return "HTTPerror/404";
		
		if ( !isAutorizado( usuario, idCliente ) )
			return "HTTPerror/404";
		else
		{
			Cliente cliente = clienteRepo.findOne( idCliente );
			
			model.addAttribute( "idCliente", cliente.getIdCliente() );
			model.addAttribute( "urlVoltarCadastro", "/admin/clientes/searches" );
			model.addAttribute( "urlVoltarPainel", "/admin/painel" );
			model.addAttribute( "nomePainel", "Painel de Admin");
			model.addAttribute( "urlInserirTitulo", "/admin/titulos/new" );
			model.addAttribute( "isAdmin", true);

			return "cliente/editar-cliente";
		}
	}

	@RequestMapping(value={ "/clientes/{idCliente}/view" }, method=RequestMethod.GET)
	@PreAuthorize("hasAuthority('DADOS_CLIENTE')")
	public String editarCliente( @PathVariable Long idCliente, ModelMap model, Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente() == null )
			return "HTTPerror/404";
		
		if ( !isAutorizado( usuario, idCliente ) )
			return "HTTPerror/404";
		else
		{
			Cliente cliente = clienteRepo.findOne( idCliente );
			
			return exportaParametrosDefaultRetornaCliente( model, cliente );
		}
	}
	
	
	@RequestMapping(value={ "/clientes/view" }, method=RequestMethod.GET)
	@PreAuthorize("hasAuthority('DADOS_CLIENTE')")
	public String editarClienteLogado( ModelMap model, Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente() == null )
			return "HTTPerror/404";
		
		Cliente cliente = usuario.getCliente();

		return exportaParametrosDefaultRetornaCliente( model, cliente );
	}



	private String exportaParametrosDefaultRetornaCliente( ModelMap model, Cliente cliente )
	{
		model.addAttribute( "idCliente", cliente.getIdCliente() );
		model.addAttribute( "urlVoltarPainel", "/principal" );
		model.addAttribute( "nomePainel", "Painel Gerencial" );
		model.addAttribute( "urlInserirTitulo", "/titulos/new" );

		return "cliente/editar-cliente";
	}
	
	
	
	@RequestMapping( value = { "/clientes", "/api/clientes" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	@PreAuthorize("hasAuthority('ADM_SISTEMA')")
	public @ResponseBody JSONListWrapper<Cliente> listClientes( 
																 @RequestParam(value="search", required=false) String search, 
																 @RequestParam(value="pageNumber", required=false) Integer pageNumber, 
																 @RequestParam(value="limit", required=false) Integer limit,
																 Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente().getIdCliente() == null )
			return null;
		
		Pageable pageable = getPageable( pageNumber, limit, "asc", "razaosocial" );
		
		Page<Cliente> clientePage = null;
		
		if ( StringUtils.isBlank( UtilsStr.notNull( search ) ) )
			clientePage = clienteRepo.findAll( pageable );
		else
		{
			String razaosocial = "%" + search + "%";
			String nomefantasia = "%" + search + "%";
			String cnpj = "%" + search + "%";

			clientePage = clienteRepo.findByRazaosocialContainingIgnoreCaseOrNomefantasiaContainingIgnoreCaseOrCnpjContaining( pageable, razaosocial, nomefantasia, cnpj );
		}
		
		JSONListWrapper<Cliente> jsonList = new JSONListWrapper<Cliente>( clientePage.getContent(), clientePage.getTotalElements() );

		return jsonList;
	}

	
	@RequestMapping( value = { "/clientes/{idCliente}", "/api/clientes/{idCliente}" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	@PreAuthorize("hasAuthority('DADOS_CLIENTE')")
	public @ResponseBody Cliente getCliente( @PathVariable Long idCliente, Principal principal  )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );

		if ( usuario == null || usuario.getCliente().getIdCliente() == null )
			return null;

		if ( isAutorizado( usuario, idCliente ) )
			return clienteRepo.findOne( idCliente );
		else
			return null;
	}
	
	
	@RequestMapping( value = { "/clientes", "/api/clientes" }, method = { RequestMethod.POST }, consumes = "application/json", produces = APPLICATION_JSON_CHARSET_UTF_8 )
	@PreAuthorize("hasAuthority('DADOS_CLIENTE')")
	public @ResponseBody String saveCliente( @RequestBody @Valid Cliente cliente, BindingResult result, Principal principal, HttpServletRequest request )
	{
		String jsonResult = null;
		
		if ( result.hasErrors() ){
			
			jsonResult = writeErrorsAsJSONErroMessage(result);	
		}
		else
		{
			try
			{
				Usuario usuario = usuarioService.getUserByPrincipal( principal );
				
				if ( usuario == null || usuario.getCliente().getIdCliente() == null )
					throw new RuntimeException("Usuário não encontrado");
				
				if ( cliente.getIdCliente() != null && cliente.getIdCliente() > 0 && !isAutorizado( usuario, cliente.getIdCliente() ) )
					throw new RuntimeException("Não é possível alterar o Cliente");
				
				cliente  = clienteService.saveCliente( cliente );
				
				jsonResult = writeObjectAsString( cliente );
			}
			catch ( Exception e )
			{
				imprimeLogErro( "Salvar Cliente", request, e );
				jsonResult = writeSingleErrorAsJSONErroMessage( "alertArea", e.getMessage() );
			}
		}

		return jsonResult;
	}
	
	
	
	@RequestMapping( value = { "/clientes/{idCliente}/condicoescomerciais", "/api/clientes/{idCliente}/condicoescomerciais" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	@PreAuthorize("hasAuthority('ADM_SISTEMA')")
	public @ResponseBody JSONListWrapper<CondicaoComercial> listCondicoesComerciais( @PathVariable Long idCliente,
																@RequestParam(value="pageNumber", required=false) Integer pageNumber, 
																@RequestParam(value="limit", required=false) Integer limit,
																Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente().getIdCliente() == null )
			return null;

		if ( !isAutorizado( usuario, idCliente ) )
			return null;
		
		Cliente cliente = clienteRepo.findOne( idCliente );
		
		Pageable pageable = getPageable( pageNumber, limit, "desc", "dataAlteracao" );
		
		Page<CondicaoComercial> ccPage = ccRepo.findByCliente( pageable, cliente );
		
		List<CondicaoComercial> list = ccPage.getContent();
		
		JSONListWrapper<CondicaoComercial> jsonList = new JSONListWrapper<CondicaoComercial>( list, ccPage.getTotalElements() );

		return jsonList;
	}

	
	
	@RequestMapping( value = { "/clientes/{idCliente}/condicoescomerciais", "/api/clientes/{idCliente}/condicoescomerciais" }, method = { RequestMethod.POST }, consumes = "application/json", produces = APPLICATION_JSON_CHARSET_UTF_8 )
	@PreAuthorize("hasAuthority('ADM_SISTEMA')")
	public @ResponseBody String saveCondicaoComercial( @RequestBody @Valid CondicaoComercial condicaoComercialVO, BindingResult result, Principal principal, HttpServletRequest request )
	{
		String jsonResult = null;
		
		if ( result.hasErrors() ){
			
			jsonResult = writeErrorsAsJSONErroMessage(result);	
		}
		else
		{
			try
			{
				Usuario usuario = usuarioService.getUserByPrincipal( principal );
				
				if ( usuario == null || usuario.getCliente().getIdCliente() == null )
					throw new RuntimeException("Usuário não encontrado");
				
				clienteService.saveCondicaoComercial( usuario, condicaoComercialVO );

				jsonResult = writeObjectAsString( condicaoComercialVO );
			}
			catch ( Exception e )
			{
				imprimeLogErro( "Salvar Condição Comercial", request, e );
				jsonResult = writeSingleErrorAsJSONErroMessage( "alertArea", e.getMessage() );
			}
		}

		return jsonResult;
	}
	


	@RequestMapping( value = { "/clientes/{idCliente}/condicoescomerciais/{idCondcom}", "/api/clientes/{idCliente}/condicoescomerciais/{idCondcom}" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	@PreAuthorize("hasAuthority('ADM_SISTEMA')")
	public @ResponseBody CondicaoComercial getCondicaoComercial( @PathVariable Long idCliente, @PathVariable Long idCondcom, Principal principal  )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );

		if ( usuario == null || usuario.getCliente().getIdCliente() == null )
			return null;

		if ( isAutorizado( usuario, idCliente ) ){
			
			CondicaoComercial cc = ccRepo.findOne( idCondcom );
			return cc;
		}
		else
			return null;
	}



	@RequestMapping( value = { "/clientes/{idCliente}/resumo", "/api/clientes/{idCliente}/resumo" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	@PreAuthorize("hasAuthority('DADOS_CLIENTE') or hasAuthority('ADM_SISTEMA')")
	public @ResponseBody ClienteResumoFinanceiroDTO getResumo( @PathVariable Long idCliente, Principal principal  )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );

		if ( usuario == null || usuario.getCliente().getIdCliente() == null )
			return null;
		
		Cliente cliente = clienteRepo.findOne( idCliente );

		if ( isAutorizado( usuario, idCliente ) )
		{
			ClienteResumoFinanceiroDTO result = clienteService.getResumoFinanceiro( cliente );
			
			return result;
		}
		else
			return null;
	}
	
	
	
	@RequestMapping( value = { "/clientes/{idCliente}/titulos", "/api/clientes/{idCliente}/titulos" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	@PreAuthorize("hasAuthority('DADOS_CLIENTE') or hasAuthority('ADM_SISTEMA')")
	public @ResponseBody JSONListWrapper<Titulo> listTitulos( 
																 @PathVariable Long idCliente, 
																 @RequestParam(value="pageNumber", required=false) Integer pageNumber, 
																 @RequestParam(value="limit", required=false) Integer limit,
																 Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente().getIdCliente() == null )
			return null;
		
		// isso nem funciona direito
		
		Pageable pageable = getPageable( pageNumber, limit, "desc", "dataVencimento" );
		
		Page<Titulo> titulosPage = tituloRepo.findAll( pageable );
		
		JSONListWrapper<Titulo> jsonList = new JSONListWrapper<Titulo>( titulosPage.getContent(), titulosPage.getTotalElements() );

		return jsonList;
	}
	


	@RequestMapping( value = { "/clientes/{idCliente}/usuarios", "/api/clientes/{idCliente}/usuarios" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	@PreAuthorize("hasAuthority('USUARIOS') or hasAuthority('ADM_SISTEMA')")
	public @ResponseBody JSONListWrapper<Usuario> getUsuariosByCliente( 
																 @PathVariable Long idCliente, 
																 @RequestParam(value="search", required=false) String search, 
																 @RequestParam(value="pageNumber", required=false) Integer pageNumber, 
																 @RequestParam(value="limit", required=false) Integer limit,
																 @RequestParam(value="sort", required=false) String sort,
																 Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente() == null )
			return null;
		
		Cliente cliente = clienteRepo.findOne( idCliente );

		if ( cliente == null )
			return null;
		
		Pageable pageable = getPageable( pageNumber, limit, "asc", sort );
		
		Page<Usuario> usuarioPage = usuarioService.findUsuarios( pageable, cliente, UsuarioTipo.GERENCIADOR, search );
		
		JSONListWrapper<Usuario> jsonList = new JSONListWrapper<Usuario>(usuarioPage.getContent(), usuarioPage.getTotalElements() );

		return jsonList;
	}




	
	@RequestMapping(value={ "/admin/relatorio-clientes", "/admin/clientes/searches" }, method=RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADM_SISTEMA')")
	public String relatorioClientes( ModelMap model, Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente() == null )
			return "HTTPerror/404";
		
		Long quantidade = clienteRepo.count();
		
		model.addAttribute( "qtdClientes", quantidade.intValue() );
		
		return "admin/relatorio-clientes";
	}



	
	@RequestMapping( value = { "/admin/clientes/searches/relatorio", "/api/clientes/searches/relatorio" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	@PreAuthorize("hasAuthority('ADM_SISTEMA')")
	public @ResponseBody JSONListWrapper<ClienteRelatorioDTO> relatorioClientes( 
																 @RequestParam(value="search", required=false) String search, 
																 @RequestParam(value="pageNumber", required=false) Integer pageNumber, 
																 @RequestParam(value="limit", required=false) Integer limit,
																 Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente().getIdCliente() == null )
			return null;
		
		Pageable pageable = getPageable( pageNumber, limit, "asc", "razaosocial" );
		
		Page<ClienteRelatorioDTO> clienteRelatorioPage = clienteService.getRelatorioCliente( pageable, search );
		
		JSONListWrapper<ClienteRelatorioDTO> jsonList = new JSONListWrapper<ClienteRelatorioDTO>( clienteRelatorioPage.getContent(), clienteRelatorioPage.getTotalElements() );

		return jsonList;
	}



	@RequestMapping( value = { "/clientes/{idCliente}/tipotaxas", "/api/clientes/{idCliente}/tipotaxas" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	public @ResponseBody JSONListWrapper<TipoTaxa> listTipoTaxas( 
															     @PathVariable Long idCliente, 
																 @RequestParam(value="pageNumber", required=false) Integer pageNumber, 
																 @RequestParam(value="limit", required=false) Integer limit,
																 Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente().getIdCliente() == null )
			return null;
			
		if ( isAutorizado( usuario, idCliente ) )
		{
			Pageable pageable = getPageable( pageNumber, limit, "asc", "descricao" );

			Page<TipoTaxa> tipotaxaPage = tipoTaxaRepo.findAll( pageable );
			
			JSONListWrapper<TipoTaxa> jsonList = new JSONListWrapper<TipoTaxa>( tipotaxaPage.getContent(), tipotaxaPage.getTotalElements() );

			return jsonList;
		}
		else
			return null;
	}



	@RequestMapping( value = { "/admin/clientes/{idCliente}/ambientes", "/api/clientes/{idCliente}/ambientes" }, method = RequestMethod.GET, produces = APPLICATION_JSON_CHARSET_UTF_8 )
	@PreAuthorize("hasAuthority('ADMINISTRAR_AMB')")
	public @ResponseBody JSONListWrapper<Ambiente> listAmbienteByCliente( 
																 @PathVariable Long idCliente,
																 @RequestParam(value="search", required=false) String search, 
																 @RequestParam(value="pageNumber", required=false) Integer pageNumber, 
																 @RequestParam(value="limit", required=false) Integer limit,
																 Principal principal )
	{
		Pageable pageable = getPageable( pageNumber, limit, "asc", "nome" );
		
		Page<Ambiente> ambientePage = clienteService.getAmbientesPorCliente( pageable, idCliente, search );
		
		if ( ambientePage == null )
			return null;
		
		JSONListWrapper<Ambiente> jsonList = new JSONListWrapper<Ambiente>(ambientePage.getContent(), ambientePage.getTotalElements() );

		return jsonList;
	}



	@RequestMapping(value={ "/admin/clientes/{idCliente}/geracobranca", "/admin/clientes/{idCliente}/geracobranca" }, method=RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADM_SISTEMA')")
	public @ResponseBody Titulo geraCobranca( @PathVariable Long idCliente, ModelMap model, Principal principal )
	{
		Usuario usuario = usuarioService.getUserByPrincipal( principal );
		
		if ( usuario == null || usuario.getCliente() == null )
			return null;
		
		Cliente cliente = clienteRepo.findOne( idCliente );
		
		if ( cliente == null )
			return null;
		
		Integer dia = usuario.getCliente().getDiaVencimento();
		
		LocalDate vencimento = LocalDate.now();
		
		if ( vencimento.getDayOfMonth() >= dia )
			vencimento = vencimento.plusMonths( 1 );

		vencimento.withDayOfMonth( dia );
		
		Titulo tit = clienteService.geraCobranca( usuario.getCliente(), UtilsDates.asUtilDate( vencimento ) );
		
		return tit;
	}

}

