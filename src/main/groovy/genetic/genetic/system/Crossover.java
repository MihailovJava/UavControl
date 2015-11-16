package genetic.genetic.system;

/**
 * Author Grinch
 * Date: 02.03.2015
 * Time: 7:09
 */
public interface Crossover {
    Person crossover(Person parent1, Person parent2, PersonFactory factory);
}
