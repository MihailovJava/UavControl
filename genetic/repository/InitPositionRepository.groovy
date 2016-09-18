package genetic.repository

import InitPositionEntity
import org.springframework.data.repository.CrudRepository


interface InitPositionRepository extends CrudRepository<InitPositionEntity,Long> {
    List<InitPositionEntity> findByNumber(Integer number)
}