package genetic.genetic.squad

import com.google.gson.Gson
import genetic.entity.FormationEntity
import genetic.genetic.system.*
import genetic.genetic.system.Populations
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import genetic.repository.FormationRepository
import genetic.spring.GeneticConfig

public class SquadSimulator {

    public static def formations6 = [
            "Клин6"   : [
                    new SquadGene(1, 3, 1),
                    new SquadGene(2, 4, 2),
                    new SquadGene(3, 5, 3),
                    new SquadGene(4, 5, 4),
                    new SquadGene(5, 4, 5),
                    new SquadGene(6, 3, 6)],
            "Колонна6": [
                    new SquadGene(4,1, 1),
                    new SquadGene(4,2, 2),
                    new SquadGene(4,3, 3),
                    new SquadGene(4,4, 4),
                    new SquadGene(4,5, 5),
                    new SquadGene(4,6, 6)],
            "Фронт6"  : [
                    new SquadGene(1,4, 1),
                    new SquadGene(2,4, 2),
                    new SquadGene(3,4, 3),
                    new SquadGene(4,4, 4),
                    new SquadGene(5,4, 5),
                    new SquadGene(6,4, 6)],
            "Пеленг6" : [
                    new SquadGene(1, 1, 1),
                    new SquadGene(2, 2, 2),
                    new SquadGene(3, 3, 3),
                    new SquadGene(4, 4, 4),
                    new SquadGene(5, 5, 5),
                    new SquadGene(6, 6, 6)],
            "Ромб6"   : [
                    new SquadGene(3, 2, 1),
                    new SquadGene(2, 3, 2),
                    new SquadGene(2, 4, 3),
                    new SquadGene(4, 5, 4),
                    new SquadGene(5, 4, 5),
                    new SquadGene(5, 3, 6)],
    ]

    public static def formations3 = [
            "Клин3"   : [
                    new SquadGene(2, 1, 1),
                    new SquadGene(1, 2, 2),
                    new SquadGene(2, 3, 3)],
            "Колонна3": [
                    new SquadGene(2,1, 1),
                    new SquadGene(2,2, 2),
                    new SquadGene(2,3, 3)],
            "Фронт3"  : [
                    new SquadGene(1,2, 1),
                    new SquadGene(2,2, 2),
                    new SquadGene(3,2, 3)],
            "Пеленг3" : [
                    new SquadGene(1, 1, 1),
                    new SquadGene(2, 2, 2),
                    new SquadGene(3, 3, 3)]
    ]

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(GeneticConfig.class);
        FormationRepository repository = context.getBean(FormationRepository.class)

        def interval =  context.getEnvironment().getProperty("interval") as Integer;
        def distance = context.getEnvironment().getProperty("distance") as Integer;
        def formations = formations3
        formations.each { key, value ->
            formations.each { key2, value2 ->
                if (key.compareTo(key2) != 0) {
                    SquadFactory factory = new SquadFactory();
                    ArrayList<Person> persons = new ArrayList<Person>();
                    def n = value2.size();
                    for (int i = 0; i < 50; i++) {

                        persons.add(factory.revivePerson(
                                new SquadMutation(),
                                new SquadFitnessFunction(value as List<SquadGene>, n, n, interval, distance),
                                new SquadCrossover(),
                                new Chromosome<SquadGene>(value2 as List<SquadGene>))
                        );
                    }

                    Person person = persons.get(0);
                    try {
                        Populations quadPopulations = new SquadPopulations(new Population(persons), factory);
                        for (int i = 0; i < 1000; i++) {
                            Person bestPerson = GeneticUtils.getBestPerson(quadPopulations.getLastPopulation());
                            if (bestPerson.getFitness() < person.getFitness()) {
                                person = bestPerson;
                            }
                            //  System.out.println("Population " + (i + 1) + ". Best person: " + bestPerson);
                            //  System.out.println("Fitness mean: " + GeneticUtils.getFitnessMean(quadPopulations.getLastPopulation()));
                            quadPopulations.nextGen();
                        }
                    } finally {
                        System.out.println("Best of the best: " + person);
                    }

                    Gson gson = new Gson();
                    repository.save(new FormationEntity(
                            id: key + key2,
                            fromFormation: key,
                            toFormation: key2,
                            jsonArray: gson.toJson(person.getChromosome()),
                            number: n,
                            interval: interval,
                            distance: distance))
                    System.out.println("from $key to $key2 " + gson.toJson(person.getChromosome()));
                }
            }
        }
    }
}
