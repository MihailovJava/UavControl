package hungarian.repository

import org.springframework.data.repository.CrudRepository

interface InitPositionRepository extends CrudRepository<hungarian.entity.InitPositionEntity,String> {
    List<hungarian.entity.InitPositionEntity> findByNumber(Integer number)
}