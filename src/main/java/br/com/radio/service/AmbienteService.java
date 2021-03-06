package br.com.radio.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.radio.dto.AlterarSenhaAmbienteDTO;
import br.com.radio.dto.EspelharAmbienteDTO;
import br.com.radio.dto.GeneroListDTO;
import br.com.radio.enumeration.VozLocucao;
import br.com.radio.model.Ambiente;
import br.com.radio.model.AmbienteConfiguracao;
import br.com.radio.model.AmbienteGenero;
import br.com.radio.model.AudioOpcional;
import br.com.radio.model.Bloco;
import br.com.radio.model.Evento;
import br.com.radio.model.EventoHorario;
import br.com.radio.model.Genero;
import br.com.radio.model.MidiaAmbiente;
import br.com.radio.model.Usuario;
import br.com.radio.programacaomusical.PosicaoComercial;
import br.com.radio.programacaomusical.PosicaoSilencio;
import br.com.radio.programacaomusical.PosicaoVinheta;
import br.com.radio.programacaomusical.ProgramacaoMusicalService;
import br.com.radio.programacaomusical.TamanhoSilencioMidia;
import br.com.radio.repository.AmbienteConfiguracaoRepository;
import br.com.radio.repository.AmbienteGeneroRepository;
import br.com.radio.repository.AmbienteRepository;
import br.com.radio.repository.BlocoRepository;
import br.com.radio.repository.ClienteRepository;
import br.com.radio.repository.EventoHorarioRepository;
import br.com.radio.repository.EventoRepository;
import br.com.radio.repository.GeneroRepository;
import br.com.radio.repository.MidiaAmbienteRepository;
import br.com.radio.service.midia.MidiaService;
import br.com.radio.util.Constantes;
import br.com.radio.util.UtilsStr;



@Service
public class AmbienteService {

	@Autowired
	private AmbienteRepository ambienteRepo;
	
	@Autowired
	private GeneroRepository generoRepo;
	
	@Autowired
	private AmbienteGeneroRepository ambienteGeneroRepo;
	
	@Autowired
	private ClienteRepository clienteRepo;
	
	@Autowired
	private MidiaService midiaService;

	@Autowired
	private MidiaAmbienteRepository midiaAmbienteRepository;
	
	@Autowired
	private BlocoRepository blocoRepo;
	
	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private AmbienteConfiguracaoRepository ambienteConfigRepo;
	
	@Autowired
	private EventoRepository eventoRepo;
	
	@Autowired
	private EventoHorarioRepository eventoHorarioRepo;
	
	@Autowired
	private ProgramacaoMusicalService progMusicalService;
	
	@Autowired
	private EntityManager entityManager;
	
	/**
	 * Esse método salva o ambiente tomando cuidado para verificar os emails e endereços.
	 * 
	 * @param ambiente
	 */
	@Transactional
	public Ambiente saveAmbiente( Ambiente ambiente )
	{
		if ( ambiente.getCliente() == null )
			throw new RuntimeException("Cliente não encontrado.");

		validaLogin( ambiente );
		
		boolean ambienteNovo = ( ambiente.getIdAmbiente() == null );
		
		if ( ambienteNovo ){
			criaExpedienteDefault( ambiente );
			
			byte[] logo = Base64.getDecoder().decode( Constantes.logoRadio );
			
			ambiente.setLogomarca( logo );
			ambiente.setLogomimetype( "image/png" );
		}

		ambiente = ambienteRepo.saveAndFlush( ambiente );
		
		if ( ambienteNovo ){
			String password = ambiente.getPassword();
			ambiente.setPassword( "" );

			usuarioService.saveUsuarioAmbientePlayer( ambiente, password );

			criaConfiguracoesDefault( ambiente );
			midiaService.associaTodasMidiasParaAmbiente( ambiente );
			
			criaBlocoDefault( ambiente );
			associaGeneroDefault( ambiente );
		}

		return ambiente; 
	}

	

	@Transactional
	public void alteraLoginSenhaAmbiente( Long idAmbiente, AlterarSenhaAmbienteDTO senhaDTO ){
		
		Ambiente ambiente = ambienteRepo.findOne( idAmbiente );
		
		if ( ambiente == null )
			throw new RuntimeException("Ambiente nãe encontrado");
		
		Ambiente existente = ambienteRepo.findByLogin( senhaDTO.getLogin() );
		
		if ( !existente.getIdAmbiente().equals( ambiente.getIdAmbiente() ) )
			throw new RuntimeException("Já existe um outro ambiente utilizando esse login.");
		
		ambiente.setLogin( senhaDTO.getLogin() );
		
		ambienteRepo.save( ambiente );
		
		usuarioService.saveUsuarioAmbientePlayer( ambiente, senhaDTO.getPassword() );
	}
	


	private void criaExpedienteDefault( Ambiente ambiente )
	{
		 ambiente.setHoraIniExpediente( 0 ); 
		 ambiente.setHoraFimExpediente( 23 ); 
		 ambiente.setMinutoIniExpediente( 0 );
		 ambiente.setMinutoFimExpediente( 59 );
	}


	private void validaLogin( Ambiente ambiente )
	{
		if ( StringUtils.isNotBlank( ambiente.getLogin() ) )
		{
			String login = ambiente.getLogin();
			
			String mensagemErro = "O login informado não está disponível por favor insira outro." ;

			Usuario usuario = usuarioService.findByLogin( login );
				
			// Update
			if ( ambiente.getIdAmbiente() != null ){
				Ambiente ambienteBanco = ambienteRepo.findByLogin( login );
				
				if ( ambienteBanco != null && !ambienteBanco.getIdAmbiente().equals( ambiente.getIdAmbiente() ) )
					throw new RuntimeException( mensagemErro );
				
				if ( usuario != null && ( usuario.getAmbiente() == null || !usuario.getAmbiente().getIdAmbiente().equals( ambiente.getIdAmbiente() ) ) )
					throw new RuntimeException( mensagemErro );
			}
			else {
				long count = ambienteRepo.countByLogin( login );
				
				if ( count > 0 )
					throw new RuntimeException( mensagemErro );
				
				if ( usuario != null )
					throw new RuntimeException( mensagemErro );
			}
			
			if ( StringUtils.contains( login , " " ) )
				throw new RuntimeException( "O login informado não deve ter espaços." );
			
			if ( !UtilsStr.isAlphaNumeric( login ) )
				throw new RuntimeException( "Existem caracteres inválidos no login. Apenas números e letras são aceitos." );
		}
	}


	private void criaConfiguracoesDefault( Ambiente ambiente )
	{
		AmbienteConfiguracao config = ambienteConfigRepo.findByAmbiente( ambiente );
				
		if ( config != null )
			return;
		
		config = new AmbienteConfiguracao();
		
		config.setAmbiente( ambiente );
		
		config.setAgendMidia( true );
		config.setAtendimento( true );
		config.setAutoplay( true );
		config.setAvancarRetornar( true );   // Apenas avançar...
		config.setChamFuncionarios( true );
		config.setChamInstantanea( true );
		config.setChamVariosFuncionarios( true );
		config.setChamVeiculo( true );
		config.setControleBlocos( true );
		config.setControleComerciais( true );
		config.setControleInstitucionais( true );
		config.setControleProgrametes( true );
		config.setGenerosByCC( true	);
		config.setHoroscopo( true );
		config.setLocutorVirtual( true );
		config.setMenuDownloads( true );
		config.setOpcionais( true );
		config.setPedidoMusical( true );
		config.setPedidoMusicalVinheta( true );
		config.setRelatoriosMidia( true );
		config.setRodoviarias( true );
		config.setSelecaoGenero( true );
		config.setBotaoStop( true );
		
		config.setControleVolumeIndividual( false );
		config.setVolumeChamadas( 100 );
		config.setVolumeComerciais( 100 );
		config.setVolumeGeral( 100 );
		config.setVolumeMusicas( 100 );
		config.setVozLocucao( VozLocucao.MASCULINA );

		ambienteConfigRepo.save( config );
		
	}
	
	private void criaBlocoDefault( Ambiente ambiente )
	{
		Bloco bloco = new Bloco();
		
		bloco.setAmbiente( ambiente );
		bloco.setIndexInstitucionais( 0 );
		bloco.setIndexProgrametes( 0 );
		bloco.setPosicaoVinheta( PosicaoVinheta.ANTES_BLOCO_COMERCIAL );
		bloco.setPosicaoComercial( PosicaoComercial.DEPOIS_MUSICAS );
		bloco.setQtdMusicas( 3 );
		bloco.setQtdComerciais( 2 );
		bloco.setIndexOpcionais( 0 );
		bloco.setTamanhoSilencio( TamanhoSilencioMidia.NENHUM );
		bloco.setPosicaoSilencio( PosicaoSilencio.NAO_INCLUIR );

		blocoRepo.saveAndFlush( bloco );
	}
	
	
	private void associaGeneroDefault( Ambiente ambiente )
	{
		List<Genero> generos = generoRepo.findAll();
		
		generos.forEach( gen -> {
			AmbienteGenero ambienteGenero = new AmbienteGenero( ambiente, gen );
			ambienteGeneroRepo.save( ambienteGenero );
		});
	}
	
	
	@Transactional
	public boolean saveGeneros( Long idAmbiente, GeneroListDTO generoList )
	{
		Ambiente ambiente = ambienteRepo.findOne( idAmbiente );
		
		boolean result = false;
		
		if ( ambiente != null )
		{
			List<Genero> generos = generoList.getLista();
			
			List<Long> idsGeneros = generos.stream().map( Genero::getIdGenero ).collect( Collectors.toList() );

			ambienteGeneroRepo.deleteByAmbienteNotInIds( idAmbiente, idsGeneros ); 
			
			generos.stream().forEach( g -> {
				Genero genero = generoRepo.findOne( g.getIdGenero() );
				
				if ( genero != null )
				{
					AmbienteGenero ambienteGenero = ambienteGeneroRepo.findByAmbienteAndGenero( ambiente, genero );
					
					if ( ambienteGenero == null )
					{
						ambienteGenero = new AmbienteGenero( ambiente, g );
						
						ambienteGeneroRepo.save( ambienteGenero );
					}
				}
			});
			
			result = true;
		}
		
		return result;
	}
	
	
	
	public Boolean saveExpediente( Long idAmbiente, Ambiente dto ) 
	{
		Ambiente ambiente = ambienteRepo.findOne( idAmbiente );
		
		ambiente.setHoraIniExpediente( dto.getHoraIniExpediente() );
		ambiente.setHoraFimExpediente( dto.getHoraFimExpediente() );
		ambiente.setMinutoIniExpediente( dto.getMinutoIniExpediente() );
		ambiente.setMinutoFimExpediente( dto.getMinutoFimExpediente() );
			
		ambienteRepo.save( ambiente );
		
		return true;
	}
	
	
	
	public Boolean isExpedienteOn( Long idAmbiente )
	{
		Ambiente ambiente = ambienteRepo.findOne( idAmbiente );
		
		return this.isExpedienteOn( ambiente );
	}
	
	
	public Boolean isExpedienteOn( Ambiente ambiente )
	{
		Boolean result = false;
		
		if ( ambiente != null )
		{
			LocalTime agora = LocalTime.now(); 
			
			LocalTime inicio = LocalTime.of( ambiente.getHoraIniExpediente(), ambiente.getMinutoIniExpediente() );
			
			LocalTime fim = LocalTime.of( ambiente.getHoraFimExpediente(), ambiente.getMinutoFimExpediente() );
			
			result = agora.isAfter( inicio ) && agora.isBefore( fim );
		}
		
		return result;
	}
	
	
	@Transactional
	public Evento saveEvento( Long idAmbiente, Evento evento )
	{
		Ambiente ambiente = ambienteRepo.findOne( idAmbiente );
		
		if ( ambiente != null )
		{
			if ( evento.getDataInicio() == null )
				throw new RuntimeException("Data de Início é obrigatória");

			if ( evento.getDataFim() == null )
				throw new RuntimeException("Data de Fim é obrigatória");
			
			if ( evento.getDataFim().before( evento.getDataInicio() ) )
				throw new RuntimeException("Data de Início não pode ser após a data de Fim.");
			
			
			evento.setAmbiente( ambiente );
			eventoRepo.save( evento );
			
			Comparator<EventoHorario> porHoraMinuto = ( um, outro ) -> {
				int i = um.getHora().compareTo( outro.getHora() );
				if ( i != 0 ) return i;
				return um.getMinuto().compareTo( outro.getMinuto() );
			};
			
			eventoHorarioRepo.deleteByEvento( evento );
			
			List<EventoHorario> horarios = evento.getHorarios();
			
			horarios = horarios.stream().filter( h -> h != null ).collect( Collectors.toList() );
			
			horarios.sort( porHoraMinuto );

			for ( EventoHorario horario : horarios )
			{
				horario.setEvento( evento );
				eventoHorarioRepo.save( horario );
			}
			
			// depois verificar os repetidos.
		}

		return evento;
	}
	
	
	
	public Bloco saveBloco( Bloco blocoVO, Ambiente ambiente )
	{
		Bloco blocoExistente = blocoRepo.findByAmbiente( ambiente ); 
		
		if ( blocoExistente != null )
			blocoVO.setIdBloco( blocoExistente.getIdBloco() ); // update
		
		blocoVO.setAmbiente( ambiente );
		blocoRepo.save( blocoVO );

		return blocoVO;
	}
	
	
	@Transactional
	public void espelharAmbiente( EspelharAmbienteDTO espelharDTO ){

		LocalDateTime inicio = LocalDateTime.now();
		
		if ( espelharDTO.getIdAmbienteTemplate() == null || espelharDTO.getIdAmbienteTemplate() == 0 )
			throw new RuntimeException("Ambiente modelo não foi selecionado");
		
		if ( espelharDTO.getIdAmbienteAtual() == null || espelharDTO.getIdAmbienteAtual() == 0 )
			throw new RuntimeException("Ambiente alvo não foi selecionado");

		Ambiente ambienteOrigem = ambienteRepo.findOne( espelharDTO.getIdAmbienteTemplate() );
		Ambiente ambienteAlvo = ambienteRepo.findOne( espelharDTO.getIdAmbienteAtual() );
		
		if ( ambienteOrigem == null )
			throw new RuntimeException("Ambiente modelo não encontrado");
		
		if ( ambienteAlvo == null )
			throw new RuntimeException("Ambiente alvo não encontrado");
		
		// Logomarca
		ambienteAlvo.setLogomarca( ambienteOrigem.getLogomarca() );
		ambienteAlvo.setLogomimetype( ambienteOrigem.getLogomimetype() );

		// Primeiro limpando o ambiente alvo
		midiaAmbienteRepository.deleteByAmbiente( ambienteAlvo );
		ambienteGeneroRepo.deleteByAmbiente( ambienteAlvo );
		ambienteConfigRepo.deleteByAmbiente( ambienteAlvo );
		
		// Blocos
		espelharBlocos( ambienteOrigem, ambienteAlvo );
		
		// Eventos
		espelharEventos( ambienteOrigem, ambienteAlvo );
		
		List<MidiaAmbiente> midiaAmbienteList = midiaAmbienteRepository.findByAmbiente( ambienteOrigem );
		List<AmbienteGenero> ambienteGeneroList = ambienteGeneroRepo.findByAmbiente( ambienteOrigem );
		
		Date hoje = new Date();
		
		// Associação de Mídias
		midiaAmbienteList.forEach( ma -> {
			MidiaAmbiente novoMa = new MidiaAmbiente( ambienteAlvo, ma.getMidia(), hoje );
			midiaAmbienteRepository.save( novoMa );
		});
		
		// Associação de Gêneros
		ambienteGeneroList.forEach( ag -> {
			AmbienteGenero novoAg = new AmbienteGenero( ambienteAlvo, ag.getGenero() );
			ambienteGeneroRepo.save( novoAg );
		});
		
		// Expediente
		ambienteAlvo.setHoraIniExpediente( ambienteOrigem.getHoraIniExpediente() );
		ambienteAlvo.setMinutoIniExpediente( ambienteOrigem.getMinutoIniExpediente() );
		ambienteAlvo.setHoraFimExpediente( ambienteOrigem.getHoraFimExpediente() );
		ambienteAlvo.setMinutoFimExpediente( ambienteOrigem.getMinutoFimExpediente() );

		// Programação Musical
		progMusicalService.espelharProgramacaoMusical(ambienteOrigem, ambienteAlvo); 
		
		// Gerando a transmissão a partir da programação 
		progMusicalService.geraTransmissao( ambienteOrigem );
		
		// Configuração
		AmbienteConfiguracao configuracaoOrigem = ambienteConfigRepo.findByAmbiente( ambienteOrigem );
		AmbienteConfiguracao configuracaoAlvo = new AmbienteConfiguracao(); 
		
		BeanUtils.copyProperties( configuracaoOrigem, configuracaoAlvo, new String[] { "idAmbConfig", "ambiente", "dataCriacao" } );

		configuracaoAlvo.setAmbiente( ambienteAlvo );
		configuracaoAlvo.setDataCriacao( new Date() );
		
		ambienteConfigRepo.save( configuracaoAlvo );

		LocalDateTime fim = LocalDateTime.now();
		
		long dif = ChronoUnit.SECONDS.between(inicio, fim);
		
		System.out.println(":) "+dif);
	}




	private void espelharBlocos( Ambiente ambienteOrigem, Ambiente ambienteAlvo )
	{
		Bloco blocoOrigem = blocoRepo.findByAmbiente( ambienteOrigem );
		Bloco blocoAlvo = blocoRepo.findByAmbiente( ambienteAlvo );
		
		List<AudioOpcional> opcionaisOrigem = blocoOrigem.getOpcionais();
		List<AudioOpcional> opcionaisAlvo = new ArrayList<AudioOpcional>(); 
		
		opcionaisOrigem.forEach( op -> opcionaisAlvo.add( op ) );
		
		BeanUtils.copyProperties( blocoOrigem, blocoAlvo, new String[] { "idBloco", "ambiente", "opcionais" } );

		blocoAlvo.setOpcionais( opcionaisAlvo );
		
		blocoRepo.save( blocoAlvo );
	}
	
	
	private void espelharEventos( Ambiente ambienteOrigem, Ambiente ambienteAlvo )
	{
		List<Evento> eventosOrigem = eventoRepo.findByAmbienteAndAtivoTrue( ambienteOrigem );
		
		eventoRepo.deleteByAmbiente( ambienteAlvo );
		
		List<Evento> eventosAlvo = new ArrayList<Evento>();
		
		Date hoje = new Date();
		
		eventosOrigem.forEach( evento -> {
			
			Evento novo = new Evento();
			BeanUtils.copyProperties( evento, novo, new String[] { "idEvento", "ambiente", "horarios" } );

			novo.setAmbiente( ambienteAlvo );
			novo.setDataCriacao( hoje );
			
			List<EventoHorario> horariosOrigem = evento.getHorarios();
			List<EventoHorario> horariosAlvo = new ArrayList<EventoHorario>();
			
			eventosAlvo.add( novo );
			
			eventoRepo.save( novo );
			
			horariosOrigem.forEach( hor -> {

				EventoHorario novoHor = new EventoHorario();
				BeanUtils.copyProperties( hor, novoHor, new String[] { "idEventoHorario", "evento" } );
				novoHor.setEvento( novo );
				
				horariosAlvo.add( novoHor );
			});
			eventoHorarioRepo.save( horariosAlvo );
		});
	}
	
}

