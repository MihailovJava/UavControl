package genetic.genetic.squad

import genetic.genetic.system.Pair
import genetic.genetic.system.Person
import genetic.genetic.system.PersonFactory
import genetic.genetic.system.Population
import genetic.genetic.system.Populations



class SquadPopulations extends Populations {


    SquadPopulations(Population firstPopulation, PersonFactory factory) {
        super(firstPopulation, factory)
    }

    @Override
    HashMap<Person, Double> getFitnessPercentage() {
        Population p = getLastPopulation();
        List<Person> persons = p.getPopulation();
        double s = 0;
        for (Person person : persons) {
            s += 1.0 / person.getFitness();
        }
        HashMap<Person, Double> percents = new HashMap<Person, Double>();
        for (Person person : p.getPopulation()) {
            percents.put(person, (s / person.getFitness()));
        }
        return percents;
    }

    @Override
    Pair<Person, Person> getParents() {
        Random r = new Random();
        HashMap<Person, Double> percents = getFitnessPercentage();
        Population population = getLastPopulation();
        List<Person> persons = population.getPopulation();
        //Collections.sort(persons, new PersonComparator());
        Person p1 = null;
        Person p2 = null;
        int index = r.nextInt(persons.size());
        while (p1 == null) {
            Person person = persons.get(index);
            if (r.nextDouble() < percents.get(person)) {
                p1 = person;
            }
            index = r.nextInt(persons.size());
        }
        index = r.nextInt(persons.size());
        while (p2 == null) {
            Person person = persons.get(index);
            if (person != p1 && r.nextDouble() < percents.get(person)) {
                p2 = person;
            }
            index = r.nextInt(persons.size());
        }
        return new Pair<Person, Person>(p1, p2);
    }

    @Override
    void nextGen() {
        List<Person> persons = new ArrayList<Person>();
        for (int i = 0; i < getLastPopulation().getPopulation().size(); i++) {
            Pair<Person, Person> pair = getParents();
            Person newPerson = pair.getKey().reproduction(pair.getValue(), getFactory());
            if (newPerson.getFitness() != 1234567.89)
                persons.add(newPerson);
            else
                i--;
        }
        addPopulation(new Population(persons));
    }
}
