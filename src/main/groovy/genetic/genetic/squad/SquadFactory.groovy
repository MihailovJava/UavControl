package genetic.genetic.squad


import genetic.genetic.system.Chromosome
import genetic.genetic.system.Person
import genetic.genetic.system.PersonFactory


class SquadFactory extends PersonFactory {

    @Override
    protected Person createPerson(Object... args) {
        return new SquadPerson((Chromosome)args[0])
    }
}
