package hungarian.entity

import com.google.gson.Gson
import hungarian.HungarianSimulator
import org.springframework.context.annotation.AnnotationConfigApplicationContext

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
        def context = new AnnotationConfigApplicationContext(hungarian.spring.HungarianConfig.class)
        def bean = context.getBean(hungarian.repository.FormationRepository.class)
        def gson = new Gson();
        def interval =  context.getEnvironment().getProperty("interval") as Integer;
        def distance = context.getEnvironment().getProperty("distance") as Integer;
        HungarianSimulator.formations5.each { key, value ->
            bean.save(new InitPositionEntity(
                    number: value.size(),
                    distance: distance,
                    interval: interval,
                    id: key,
                    jsonPosition: gson.toJson(value)))
        }
    }

}

