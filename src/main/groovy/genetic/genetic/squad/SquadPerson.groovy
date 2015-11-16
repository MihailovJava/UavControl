package genetic.genetic.squad

import genetic.genetic.system.Chromosome
import genetic.genetic.system.Person


class SquadPerson extends Person {
    private int count;
    Random r = new Random();

    public SquadPerson(Chromosome chromosome){
        setChromosome(chromosome);
        count = chromosome.getSize();
    }

    @Override
    int getSeparator() {
        return r.nextInt(count-1)+1;
    }
}
