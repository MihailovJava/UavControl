package hungarian

import com.google.gson.Gson


import hungarian.entity.FormationEntity
import hungarian.repository.FormationRepository
import hungarian.spring.HungarianConfig
import org.springframework.context.annotation.AnnotationConfigApplicationContext

class HungarianSimulator {
    public static def formations6 = [
            "Клин6"   : [
                    new SquadPosition(1, 3, 1),
                    new SquadPosition(2, 4, 2),
                    new SquadPosition(3, 5, 3),
                    new SquadPosition(4, 5, 4),
                    new SquadPosition(5, 4, 5),
                    new SquadPosition(6, 3, 6)],
            "Колонна6": [
                    new SquadPosition(4, 1, 1),
                    new SquadPosition(4, 2, 2),
                    new SquadPosition(4, 3, 3),
                    new SquadPosition(4, 4, 4),
                    new SquadPosition(4, 5, 5),
                    new SquadPosition(4, 6, 6)],
            "Фронт6"  : [
                    new SquadPosition(1, 4, 1),
                    new SquadPosition(2, 4, 2),
                    new SquadPosition(3, 4, 3),
                    new SquadPosition(4, 4, 4),
                    new SquadPosition(5, 4, 5),
                    new SquadPosition(6, 4, 6)],
            "Пеленг6" : [
                    new SquadPosition(1, 1, 1),
                    new SquadPosition(2, 2, 2),
                    new SquadPosition(3, 3, 3),
                    new SquadPosition(4, 4, 4),
                    new SquadPosition(5, 5, 5),
                    new SquadPosition(6, 6, 6)],
            "Ромб6"   : [
                    new SquadPosition(3, 2, 1),
                    new SquadPosition(2, 3, 2),
                    new SquadPosition(2, 4, 3),
                    new SquadPosition(4, 5, 4),
                    new SquadPosition(5, 4, 5),
                    new SquadPosition(5, 3, 6)],
    ]
    public static def formations5 = [
            "Клин5"   : [
                    new SquadPosition(1, 2, 1),
                    new SquadPosition(2, 3, 2),
                    new SquadPosition(3, 4, 3),
                    new SquadPosition(4, 3, 4),
                    new SquadPosition(5, 2, 5)],
            "Колонна5": [
                    new SquadPosition(3, 1, 1),
                    new SquadPosition(3, 2, 2),
                    new SquadPosition(3, 3, 3),
                    new SquadPosition(3, 4, 4),
                    new SquadPosition(3, 5, 5)],
            "Фронт5"  : [
                    new SquadPosition(1, 3, 1),
                    new SquadPosition(2, 3, 2),
                    new SquadPosition(3, 3, 3),
                    new SquadPosition(4, 3, 4),
                    new SquadPosition(5, 3, 5)],
            "Пеленг5" : [
                    new SquadPosition(1, 1, 1),
                    new SquadPosition(2, 2, 2),
                    new SquadPosition(3, 3, 3),
                    new SquadPosition(4, 4, 4),
                    new SquadPosition(5, 5, 5)]
    ]

    public static def formations3 = [
            "Клин3"   : [
                    new SquadPosition(1, 1, 1),
                    new SquadPosition(2, 2, 2),
                    new SquadPosition(3, 1, 3)],
            "Колонна3": [
                    new SquadPosition(2, 3, 1),
                    new SquadPosition(2, 2, 2),
                    new SquadPosition(2, 1, 3)],
            "Фронт3"  : [
                    new SquadPosition(1, 2, 1),
                    new SquadPosition(2, 2, 2),
                    new SquadPosition(3, 2, 3)],
            "Пеленг3" : [
                    new SquadPosition(1, 3, 1),
                    new SquadPosition(2, 2, 2),
                    new SquadPosition(3, 1, 3)]
    ]

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(HungarianConfig.class);
        FormationRepository repository = context.getBean(FormationRepository.class)

        def interval = context.getEnvironment().getProperty("interval") as Integer;
        def distance = context.getEnvironment().getProperty("distance") as Integer;
        def formations = formations5
        formations.each { key, value ->
            formations.each { key2, value2 ->
                if (key.compareTo(key2) != 0) {
                    float[][] costMatrix = getCostMatrix(value, value2, distance, interval)
                    def formation = new Hungarian(costMatrix).execute()
                    List<SquadPosition> geneList = new ArrayList<>()
                    def dist = 0
                    for (int i = 0; i < value.size(); i++) {
                        for (int j = 0; j < value.size(); j++) {
                            int from = formation[j][0]
                            if (from == i) {
                                int to = formation[j][1]

                                SquadPosition toGene = value2.get(to)
                                SquadPosition fromGene = value.get(from)
                                geneList.add(toGene)
                                int dI = fromGene.i - toGene.i
                                int dJ = fromGene.j - toGene.j
                                dist += Math.sqrt(dI * dI * distance + dJ * dJ * interval)
                            }
                        }


                    }

                    Gson gson = new Gson();
                    repository.save(new FormationEntity(
                            id: key + key2,
                            fromFormation: key,
                            toFormation: key2,
                            jsonArray: gson.toJson(geneList),
                            number: value.size(),
                            interval: interval,
                            distance: distance,
                            sumLength: dist))
                    System.out.println("from $key to $key2 " + dist);
                }
            }
        }
    }

    static float[][] getCostMatrix(ArrayList<SquadPosition> from, ArrayList<SquadPosition> to, float D, float I) {
        def costMatrix = new float[from.size()][to.size()]
        for (int i = 0; i < from.size(); i++) {
            for (int j = 0; j < to.size(); j++) {
                int dI = from.get(i).i - to.get(j).i
                int dJ = from.get(i).j - to.get(j).j
                costMatrix[i][j] = Math.sqrt(dI * dI * D + dJ * dJ * I)
            }
        }
        return costMatrix

    }
}
