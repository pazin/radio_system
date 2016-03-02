package br.com.radio.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.radio.model.Ambiente;
import br.com.radio.model.Categoria;
import br.com.radio.model.Genero;
import br.com.radio.model.Midia;

public interface MidiaRepository extends JpaRepository<Midia, Long> {

	Page<Midia> findByAmbientesAndCategoriasAndValidoTrue( Pageable pageable, Ambiente ambiente, Categoria categoria );
	
	List<Midia> findByAmbientesAndCategoriasAndValidoTrue( Ambiente ambiente, Categoria categoria );
	
	Page<Midia> findByAmbientesAndCategorias_codigoAndValidoTrue( Pageable pageable, Ambiente ambiente, String codigo );

	List<Midia> findByAmbientesAndCategorias_codigoAndValidoTrue( Ambiente ambiente, String codigo );
	
	Page<Midia> findByAmbientesAndValidoTrue( Pageable pageable, Ambiente ambiente );
	
	Page<Midia> findByAmbientesAndNomeContainingAndValidoTrue( Ambiente ambiente, String nome, Pageable pageable );
	
	Page<Midia> findByAmbientesAndNomeContainingAndCategoriasInAndValidoTrue( Ambiente ambiente, String nome, List<Categoria> categorias, Pageable pageable );
	
	Midia findByFilehash( String filehash );
	
	// Procurando por midias desse ambiente, na categoria indicada, nos generos indicados, sem repetição
	@Query("SELECT m FROM Midia m JOIN m.ambientes a JOIN m.categorias c JOIN m.generos g WHERE m.valido = true AND a = ?1 AND c = ?2 AND g IN ?3 group by m ")
	List<Midia> findByAmbientesAndCategoriasAndGenerosInGroupBy( Ambiente ambiente, Categoria categoria, Set<Genero> genero );
	
	// Procurando por midias desse ambiente, na categoria indicada, nos generos indicados, COM EXCEÇÃO DAS JÁ TOCADAS, sem repetição
	@Query("SELECT m FROM Midia m JOIN m.ambientes a JOIN m.categorias c JOIN m.generos g WHERE m.valido = true AND a = ?1 AND c = ?2 AND g IN ?3 AND m NOT IN ?4 group by m ")
	List<Midia> findByAmbientesAndCategoriasAndGenerosInAndMidiaNotInGroupBy( Ambiente ambiente, Categoria categoria, Set<Genero> genero, Set<Midia> midiasJaTocadas );

	// Procurando por midias desse ambiente, na categoria indicada, nos generos indicados, COM repetição
//	@Query("SELECT m.idMidia FROM Midia m JOIN m.ambientes a JOIN m.categorias c JOIN m.generos g WHERE a = ?1 AND c = ?2 AND g IN ?3 ")
//	List<Long> findIdsByAmbientesAndCategoriasAndGenerosIn( Ambiente ambiente, Categoria categoria, Set<Genero> genero );
	
	
	// Admin
	Page<Midia> findByCategoriasAndValidoTrue( Pageable pageable, Categoria categoria );
}
