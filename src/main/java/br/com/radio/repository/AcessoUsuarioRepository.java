package br.com.radio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import br.com.radio.model.AcessoUsuario;
import br.com.radio.model.Usuario;

public interface AcessoUsuarioRepository extends JpaRepository<AcessoUsuario, Long> {
	
	List<AcessoUsuario> findBySessionIdAndUsuarioAndDataLogoutIsNull( String sessionId, Usuario usuario );

	List<AcessoUsuario> findBySessionIdAndDataLogoutIsNull( String sessionId );
	
	@Modifying(clearAutomatically=true)
	@Query(value = "update acesso_usuario set datalogout = datacriacao + interval '10 minutes' where id_usuario = ?1 and datalogout is null and id_acesso not in ( select max(id_acesso) from acesso_usuario where id_usuario = ?1 and datalogout is null )", nativeQuery=true)
	int updateAcessosSemLogout(Long idUsuario);

	@Query("select a.usuario.idUsuario from AcessoUsuario a where dataLogout is null Group By a.usuario.idUsuario Having count(a) > 1 ")
	List<Long> findIdUsuariosSemLogout();

}