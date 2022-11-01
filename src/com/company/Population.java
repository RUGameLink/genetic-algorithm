package com.company;
import java.util.*;

public class Population {
    List<Individidual> population = new ArrayList<>(); // список особей в популяции
    int numberOfIndividual;  // количество особей
    int numberOfSigns; // количество признаков

    private double[] requiredFunction; // искомая функция (Y)
    private double[] xForRequiredFunc; // x для искомой функции

    public Population(int numberOfIndividual, int numberOfSigns) {
        this.numberOfIndividual = numberOfIndividual;
        this.numberOfSigns = numberOfSigns;

        // Находим искомую функцию
        xForRequiredFunc = WorkWithNumbers.getArrayFromTo(0.1, 0.1, 100);
        requiredFunction = WorkWithNumbers.getFunction(xForRequiredFunc);
    }

    public Population() {
    }

    /**
     * Запускает генетический алгоритм. Здесь в цикле размножение, мутация и удаление особей.
     * Остановка цикла в случае, если эвклидово рассояние <= 0.5
     */
    public void startGeneticAlg(){
        // Здесь в цикле будем размножать, уничтожать особи до тех пор, пока эвклидово расстояние не будет равно 0,5.
        final double EUCLIDDIST = 0.07; // Остановить мутационный процесс при достижении эвклидова расстояния = 0,5.
        double minEuclid; // посчитанное эвклидово расстояние для особей
        Individidual indWithMinEuclidDist; // особь с минимальным эвклидовым расстоянием

        int count = 0; // кол-во популяций, прошедших цикл
        do {
            // 3. Создать алгоритм, размножения и мутации, способный на каждом шаге удваивать популяцию.
            crossoverByTwoPointOperator(); // двухточечный кроссовер

            mutatePopulation();

            // 4. Уничтожать половину удвоенной популяции, эвклидово расстояние признаков
            // которых максимально удалено от искомой функции.
            delete();

            // тут просто выводим, чтобы отслеживать набор особей
            System.out.println("************* Популяция после удаления *************");
            for (int i = 0; i < population.size(); i++){
                Individidual ind = population.get(i);
                System.out.println(i + ") " + ind.toString() + " euclid=" + ind.getEuclidDist());
            }

            // Ищем минимальное эвклидовое расстояние
            indWithMinEuclidDist = getIndWithMinEuclidDist(); // особь с min dist
            minEuclid = indWithMinEuclidDist.getEuclidDist();
            minEuclid = WorkWithNumbers.round(minEuclid, 10); // округляем
            System.out.println("МИНИМАЛЬНОЕ ЭВКЛИДОВОЕ РАССТОЯНИЕ = " + minEuclid);
            count++;

        } while (minEuclid > EUCLIDDIST); // 5. Остановить мутационный процесс при достижении эвклидова расстояния = 0,5.

        System.out.println("Количество поколений, прошедших через цикл: " + count);
    }

    /**
     * Создаем начальную популяцию. Создаем 20 особей и заполняем признаки особей рандомными числами при помощи getRandomArray()
     */
    public void createPopulation(){
        // создаем 20 особей, у каждой 100 признаков
        for (int i = 0; i < numberOfIndividual; i++){
            double[] signs = WorkWithNumbers.getRandomArray(numberOfSigns, -1.0, 1.0); // массив признаков для текущей особи
            Individidual individidual = new Individidual(numberOfSigns); // создаем особь

            individidual.setSigns(signs); // добавляем особи признаки
            population.add(individidual); // добавляем особь в популяцию
        }
    }

    /**
     * Размножение (Двухточечный ОПЕРАТОР КРОССИНГОВЕРА)
     * В каждой хромосоме определяются две точки оператора кроссинговера,
     * и хромосомы обмениваются участками, расположенными между двумя точками оператора кроссинговера.
     * Точки оператора кроссинговера в двухточечном операторе кроссинговера также определяются случайно.
     */
    public void crossoverByTwoPointOperator(){
        // 1. выбираются 2 особи случайно из текущей популяции
        // 1.1 для этого перемешаем arraуlist
        Collections.shuffle(population);

        // 1.2 и будем брать особи по порядку и попарно скрещивать
        Population resOfCrossover = new Population(); // здесь храним результат скрещивания, новые сооби
        for (int i = 0; i < population.size() - 1; i += 2){
            // берем родителей
            Individidual parent1 = population.get(i);
            Individidual parent2 = population.get(i + 1);

            // скрещиваем и получаем еще две особи
            Individidual[] children = Individidual.crossoverByTwoPoints(parent1, parent2);
            // вносим особи в популяцию
            resOfCrossover.addInd(children[0]);
            resOfCrossover.addInd(children[1]);
        }

        // после того как посчитали новые особи добавляем их в общую популяцию:
        for (Individidual ind:
                resOfCrossover.getPopulation()) {
            population.add(ind);
        }
    }
    /**
     * Метод для добавления особи в популяцию
     * @param ind - особь, которую нужно добавить
     */
    public void addInd(Individidual ind) {
        population.add(ind);
    }

    /**
     * Проводим мутацию для каждой особи
     */
    public void mutatePopulation(){
        for (Individidual el:
                population) {
            el.mutateInStepIncrease(requiredFunction);
        }
    }

    /**
     * Метод для поиска и удаления максимально отдаленных особей. Сначала сортируем по эвклидову
     * расстоянию, а затем удаляем вторую часть с наибольшими значениями.
     */
    public void delete() {
        // здесь сортируем популяцию по возрастанию эвклидового расстояния
        sortByEuclid();

        // удаляем половину (элементы, которые идут после среднего)
        int mid = population.size() / 2; // индекс среднего элемента
        System.out.println("mid=" + mid);

        // формируем новую популяцию
        // в нее включаем половину особей (тех, у кого эвклидово расстояние минимально)
        ArrayList<Individidual> newPopulation = new ArrayList<>();
        for (int i = 0; i < mid; i++){
            newPopulation.add(population.get(i));
        }

        // теперь новая популяция заняла место старой:
        population = newPopulation;
    }

    /**
     * Функция для сортировки особей по возрастанию по эвклидовому расстоянию между
     * признаками особи и искомой функцией
     */
    public void sortByEuclid(){
        // считаем эвклидово расстояние для всех особей
        for (Individidual ind:
                population) {
            ind.countEuclidDistanceForSignsWithRequiredFunc(requiredFunction); // сравниваем с искомой функцией
        }

        // здесь сортируем популяцию по возрастанию
        Collections.sort(population, new Comparator<Individidual>() {
            @Override
            public int compare(Individidual o1, Individidual o2) {
                return o1.compareTo(o2);
            }
        });
    }

    @Override
    public String toString() {
        String formatted = "";

        for (int i = 0; i < population.size(); i++){
            formatted += i + ") ";
            formatted += population.get(i).toString();
            formatted += "\n";
        }

        return formatted;
    }

    /**
     * Выводит особь с минимальным эвклидовым расстоянием.
     * @return особь с наименьшим значением эвклидового расстояния среди всей популяции
     */
    public Individidual getIndWithMinEuclidDist(){
        // минимум = первому элементу
        Individidual indWithMinEuclid = population.get(0);

        // смотрим по всей популяции
        for (Individidual ind:
                population) {
            double minEuclidDist = indWithMinEuclid.getEuclidDist(); // смотрим минимум на данный момент
            if (ind.getEuclidDist() < minEuclidDist){ // если он меньше то это новый минимум
                indWithMinEuclid = ind;
            }
        }

        return indWithMinEuclid;
    }

    public List<Individidual> getPopulation() {
        return population;
    }

    public double[] getRequiredFunction() {
        return requiredFunction;
    }

    public double[] getxForRequiredFunc() {
        return xForRequiredFunc;
    }

}
