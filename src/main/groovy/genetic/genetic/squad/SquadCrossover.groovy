package genetic.genetic.squad

import genetic.genetic.system.Chromosome
import genetic.genetic.system.Crossover
import genetic.genetic.system.Person
import genetic.genetic.system.PersonFactory


class SquadCrossover implements Crossover {


    @Override
    Person crossover(Person parent1, Person parent2, PersonFactory factory) {
        Random r = new Random();
        Chromosome<SquadGene> chromosome = new Chromosome<SquadGene>(parent1.getChromosome().getChromosome());
        int separator = parent1.getSeparator();
        if (r.nextBoolean()){
            List<SquadGene> leftPart = parent1.getChromosome().getLeftPart(separator);
            List<SquadGene> rightPart = parent2.getChromosome().getRightPart(separator);
            for (int i = 0; i < separator; i++){
                chromosome.setGene(i, leftPart.get(i));
            }
            for (int i = 0; i < rightPart.size(); i++){
                chromosome.setGene(i + separator, rightPart.get(i));
            }
        }else{
            List<SquadGene> leftPart = parent2.getChromosome().getLeftPart(separator);
            List<SquadGene> rightPart = parent1.getChromosome().getRightPart(separator);
            for (int i = 0; i < separator; i++){
                chromosome.setGene(i, leftPart.get(i));
            }
            for (int i = 0; i < rightPart.size(); i++){
                chromosome.setGene(i + separator, rightPart.get(i));
            }
        }

        Person newPerson = factory.revivePerson(parent2.getMutation(), parent1.getFitnessFunction(), parent1.getCrossover(),chromosome);

        return newPerson;
    }
}
