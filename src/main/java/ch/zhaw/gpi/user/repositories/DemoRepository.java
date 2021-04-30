package ch.zhaw.gpi.user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import ch.zhaw.gpi.user.entities.DemoEntity;

/**
 * Repository-Klasse für Demo-Entität, welche CRUD-Operationen auf die dahinterliegende Datenbank kapselt
 * 
 * @author scep
 */
@CrossOrigin
@RepositoryRestResource
public interface DemoRepository extends JpaRepository<DemoEntity, Long>{

}
