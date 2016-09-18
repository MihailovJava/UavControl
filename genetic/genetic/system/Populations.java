package genetic.genetic.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import genetic.genetic.system.Pair;
import genetic.genetic.system.Person;
import genetic.genetic.system.PersonFactory;
import genetic.genetic.system.Population;

/**
 * Author Grinch
 * Date: 01.03.2015
 * Time: 18:38
 */
public abstract class Populations {
    List<Population> populations;
    PersonFactory factory;

    public Populations(Population firstPopulation, PersonFactory factory){
        populations = new ArrayList<Population>();
        populations.add(firstPopulation);
        this.factory = factory;
    }

    public void addPopulation(Population population){
        if (population != null){
            populations.add(population);
        }
    }

    public abstract HashMap<Person,Double> getFitnessPercentage();
    public abstract Pair<Person,Person> getParents();
    public abstract void nextGen();

    public Population getPopulation(int index){
        return populations.get(index);
    }
    public Population getLastPopulation(){
        return getPopulation(populations.size() - 1);
    }

    public PersonFactory getFactory() {
        return factory;
    }
}
