package hungarian.repository

import org.springframework.data.repository.CrudRepository

interface FormationRepository extends CrudRepository<hungarian.entity.FormationEntity,String> {
    hungarian.entity.FormationEntity findByFromFormationAndToFormation(String from,String to)
}