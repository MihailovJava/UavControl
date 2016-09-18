package genetic.genetic.system;

import java.util.List;

/**
 * Author Grinch
 * Date: 03.03.2015
 * Time: 14:58
 */
public class GeneticUtils {

    public static Person getBestPerson(Population p){
        List<Person> persons = p.getPopulation();
        Person bestPerson = persons.get(0);
        for (Person person : persons){
            if (person.getFitness() < bestPerson.getFitness()){
                bestPerson = person;
            }
        }
        return bestPerson;
    }

    public static double getFitnessMean(Population p){
        List<Person> persons = p.getPopulation();
        double mean = 0;
        for (Person person : persons){
            mean += person.getFitness();
        }
        return mean / persons.size();
    }
}
