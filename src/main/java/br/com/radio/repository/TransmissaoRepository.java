package br.com.radio.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import br.com.radio.enumeration.StatusPlayback;
import br.com.radio.model.Ambiente;
import br.com.radio.model.Transmissao;

public interface TransmissaoRepository extends JpaRepository<Transmissao, Long> {

	Transmissao findByIdTransmissaoAndAmbienteAndLinkativoTrue( Long idTransmissao, Ambiente ambiente );
	
	
	// Esse método vai tentar encontrar um registro que esteja mais ou menos no mesmo horário do servidor e ainda não foi tocado
	@Query(value="select t.* from transmissao t where t.id_ambiente = ?1 and linkativo = true and clock_timestamp() between t.dataprevisaoplay and t.dataprevisaoplay + ( t.duracao * interval '1 second' ) order by t.dataprevisaoplay asc limit 1 ", nativeQuery=true )
	Transmissao findByIdAmbienteAndLinkativoTrueAndPrevisaoAtual( Long idAmbiente );
	
	// Esse método vai tentar encontrar um registro que esteja mais ou menos no horário que foi passado por parametro ( e que não seja um evento ) .... método utilizado para inserir eventos na programação 
	@Query(value="select t.* from transmissao t where t.id_ambiente = ?1 and linkativo = true and ?2 between t.dataprevisaoplay and t.dataprevisaoplay + ( t.duracao * interval '1 second' ) and t.id_eventohorario is null order by t.dataprevisaoplay desc limit 1 ", nativeQuery=true )
	Transmissao findByIdAmbienteAndLinkativoTrueAndDataPrevisaoplay( Long idAmbiente, Date dataPrevisaoPlay );

	// Esse método vai verificar se já tem uma transmissão de evento programada próxima dessa posicao
	@Query(value="select t.* from transmissao t where t.id_ambiente = ?1 and linkativo = true and ( posicaoplay > ?2 and posicaoplay < ?2 + 1 ) and id_eventohorario is not null order by posicaoplay desc limit 1 ", nativeQuery= true)
	Transmissao findByIdAmbienteAndLinkativoTrueAndEventoJaProgramado( Long idAmbiente, double posicaoplay );
	
	
	// Esse método vai tentar encontrar o primeiro registro que exista pra tocar NO DIA.... 
	Transmissao findFirstByAmbienteAndLinkativoTrueAndDiaPlayOrderByIdTransmissaoAscPosicaoplayAsc( Ambiente ambiente, Date diaPlay );

	
	// Pelo fato de eu ainda não ter o ID do registro preciso fazer o update com o link depois de ter gravado a transmissao
	@Modifying(clearAutomatically=true)
	@Query("update Transmissao t set t.link = ?1||t.idTransmissao||'/midia' where t.link is null")
	int setLinkFor(String url);

	List<Transmissao> findByAmbienteAndLinkativoOrderByProgramacao_idProgramacaoAscPosicaoplayAsc( Ambiente ambiente, Boolean linkativo );
	
	List<Transmissao> findByAmbienteAndStatusPlaybackAndDiaPlayBetweenAndMidiaNotNull( Ambiente ambiente, StatusPlayback statusplay, Date diaPlayIni, Date diaPlayFim );

	@Modifying(clearAutomatically=true)
	@Query("update Transmissao t set t.statusPlayback = 'IGNORADA' where t.ambiente = ?1 and t.linkativo = true ")
	int setStatusIgnoradas( Ambiente ambiente );    // Desativando os links para gerar uma nova transmissão
	
	@Modifying(clearAutomatically=true)
	@Query("update Transmissao t set t.linkativo = false where t.ambiente = ?1 and t.linkativo = true ")
	int setLinkInativo( Ambiente ambiente );    // Desativando os links para gerar uma nova transmissão

	@Modifying(clearAutomatically=true)
	@Query("update Transmissao t set t.statusPlayback = ?1 where t.idTransmissao = ?2 ")
	int setStatus( StatusPlayback status, Long idTransmissao );  
	
	// Para encontrar a próxima musica
//	Transmissao findByAmbienteAndLinkativoTrueAndPosicaoplay( Ambiente ambiente, Double posicaoplay );
	
	// Para encontrar a próxima música agora com eventos	
	@Query(value="select t.* from transmissao t where t.id_ambiente = ?1 and t.linkativo = true and t.posicaoplay > ?2 order by posicaoplay asc, id_transmissao desc limit 1 ", nativeQuery=true)
	Transmissao findByAmbienteAndLinkativoTrueAndPosicaoplay( Long idAmbiente, Double posicaoplay );
	
	
	@Modifying(clearAutomatically=true)
	@Query("update Transmissao t set t.linkativo = false, t.downloadcompleto = true, t.dataFinishPlay = clock_timestamp(), t.statusPlayback = 'IGNORADA' where t.ambiente = ?1 and t.linkativo = true and t.idTransmissao < ?2 ")
	int setLinkInativoAnteriores( Ambiente ambiente, Long idTransmissao );    

}
