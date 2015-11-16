package genetic.entity

import com.google.gson.Gson
import genetic.genetic.squad.SquadSimulator
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import genetic.repository.FormationRepository
import genetic.spring.Config

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = 'initposition')
class InitPositionEntity {

    @Id
    String id

    @Column
    Integer number

    @Column
    String jsonPosition


    public static void main(String[] args) {
        def context = new AnnotationConfigApplicationContext(Config.class)
        def bean = context.getBean(FormationRepository.class)
        def gson = new Gson();
        SquadSimulator.formations.each { key, value ->
            bean.save(new InitPositionEntity(
                    number: 6,
                    id: key,
                    jsonPosition: gson.toJson(value)))
        }
    }

}

