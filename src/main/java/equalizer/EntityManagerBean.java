package equalizer;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;

public class EntityManagerBean {
	@Autowired
	public EntityManager entityManager;
}
