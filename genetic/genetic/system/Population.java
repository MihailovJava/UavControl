package genetic.genetic.system;

import java.util.List;

/**
 * Author Grinch
 * Date: 01.03.2015
 * Time: 18:30
 */
public class Population {
    List<Person> population;

    public List<Person> getPopulation(){
        return population;
    }

    public Population(List<Person> population){
        this.population = population;
        for (Person p : population){
            p.setPopulation(this);
        }
    }
}
