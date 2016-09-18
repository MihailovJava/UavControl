package hungarian.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "formation")
public class FormationEntity {

    @Id
    String id

    @Column
    String fromFormation

    @Column
    String toFormation
    
    @Column
    Integer number

    @Column
    Double distance

    @Column
    Double interval

    @Column
    String jsonArray

    @Column
    Double sumLength

}
