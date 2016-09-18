package genetic.genetic.system;

import java.util.Comparator;

/**
 * Author Grinch
 * Date: 07.03.2015
 * Time: 15:13
 */
public class PersonComparator implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        return (int)(o1.getFitness() - o2.getFitness());
    }
}
