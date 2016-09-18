package genetic.genetic.system;

/**
 * Author Grinch
 * Date: 01.03.2015
 * Time: 18:22
 */
public abstract class Person {
    Chromosome chromosome;
    Mutation mutation;
    FitnessFunction fitnessFunction;
    Crossover crossover;
    private double fitness;
    Population population;

    public Chromosome getChromosome(){
        return chromosome;
    }

    public void setChromosome(Chromosome chromosome){
        this.chromosome = chromosome;
    }

    public void setMutation(Mutation mutation) {
        this.mutation = mutation;
    }

    public Mutation getMutation() {
        return mutation;
    }

    void mutation() {
        mutation.mutation(this);
    }

    public void setFitness(FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public FitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }

    public double getFitness(){
        return fitness;
    }

    double fitness() {
        fitness = fitnessFunction.getFitness(this);
        return fitness;
    }

    public void setCrossover(Crossover crossover) {
        this.crossover = crossover;
    }

    public Crossover getCrossover() {
        return crossover;
    }

    public Person reproduction(Person person, PersonFactory factory) {
        return crossover.crossover(this,person, factory);
    }

    public Population getPopulation() {
        return population;
    }

    public void setPopulation(Population population) {
        this.population = population;
    }

    public abstract int getSeparator();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Object o : getChromosome().chromosome){
            sb.append(o).append(",");
        }
        sb.deleteCharAt(sb.length()-1).append("] Fitness: ").append(getFitness());
        return sb.toString();
    }
}
