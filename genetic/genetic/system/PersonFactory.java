package genetic.genetic.system;

/**
 * Author Grinch
 * Date: 01.03.2015
 * Time: 19:07
 */
public abstract class PersonFactory {

    public Person revivePerson(Mutation mutation, FitnessFunction fitnessFunction, Crossover crossover, Object... args){
        Person person = createPerson(args);
        person.setMutation(mutation);
        person.setFitness(fitnessFunction);
        person.setCrossover(crossover);
        //person.fitness();
        person.mutation();
        person.fitness();
        return person;
    }

    protected abstract Person createPerson(Object... args);
}
