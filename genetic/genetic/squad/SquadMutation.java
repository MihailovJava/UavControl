package genetic.genetic.squad;

import genetic.genetic.system.Mutation;
import genetic.genetic.system.Person;

import java.util.Random;

public class SquadMutation implements Mutation {


    @Override
    public void mutation(Person person) {
        if(new Random().nextBoolean()) {
            int size = person.getChromosome().getSize();
            int pos1 = new Random().nextInt(size);
            int pos2 = new Random().nextInt(size);
            Object a =  person.getChromosome().getGene(pos1);
            person.getChromosome().setGene(pos1, person.getChromosome().getGene(pos2));
            person.getChromosome().setGene(pos2, a);
        }
    }
}
