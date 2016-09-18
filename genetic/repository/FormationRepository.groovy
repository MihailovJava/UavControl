package genetic.repository

import FormationEntity
import org.springframework.data.repository.CrudRepository


interface FormationRepository extends CrudRepository<FormationEntity,String> {
    FormationEntity findByFromFormationAndToFormation(String from,String to)
}