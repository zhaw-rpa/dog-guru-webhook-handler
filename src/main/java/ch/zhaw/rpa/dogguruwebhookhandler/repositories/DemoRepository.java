package ch.zhaw.rpa.dogguruwebhookhandler.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import ch.zhaw.rpa.dogguruwebhookhandler.entities.DemoEntity;


/**
 * Repository-Klasse für Demo-Entität, welche CRUD-Operationen auf die dahinterliegende Datenbank kapselt
 * 
 * @author scep
 */
@CrossOrigin
@RepositoryRestResource
public interface DemoRepository extends JpaRepository<DemoEntity, Long>{

}
