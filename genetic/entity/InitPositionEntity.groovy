package genetic.entity

import com.google.gson.Gson
import SquadSimulator
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import FormationRepository
import genetic.spring.GeneticConfig

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

    @Column
    Integer distance

    @Column
    Integer interval


    public static void main(String[] args) {
        def context = new AnnotationConfigApplicationContext(GeneticConfig.class)
        def bean = context.getBean(FormationRepository.class)
        def gson = new Gson();
        def interval =  context.getEnvironment().getProperty("interval") as Integer;
        def distance = context.getEnvironment().getProperty("distance") as Integer;
        SquadSimulator.formations3.each { key, value ->
            bean.save(new InitPositionEntity(
                    number: 3,
                    distance: distance,
                    interval: interval,
                    id: key,
                    jsonPosition: gson.toJson(value)))
        }
    }

}

