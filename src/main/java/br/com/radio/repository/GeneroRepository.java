package br.com.radio.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.radio.model.Genero;

public interface GeneroRepository extends JpaRepository<Genero, Long> {

	List<Genero> findAllByOrderByNome();

	Page<Genero> findAllByOrderByNome( Pageable page );
	
	List<Genero> findFirst10By();
	
	List<Genero> findFirst5By();

	List<Genero> findByNomeIn( List<String> generosList );
	
	List<Genero> findByIdGeneroIn( List<Long> idList );
}
	