package com.company;

import java.math.BigDecimal;
import java.util.Arrays;

public class Individidual implements Comparable<Individidual>{
    int numberOfSigns; // количество признаков
    double[] signs; // массив признаков для текущей особи

    double euclidDist; // эвклидово расстояние между особью и искомой функцией

    public Individidual(int numberOfSigns) {
        this.numberOfSigns = numberOfSigns;
        signs = new double[numberOfSigns];
    }

    public void setSigns(double[] signs) {
        this.signs = signs;
    }

    /**
     * СКРЕЩИВАНИЕ (двуточечный оператор кроссинговера)
     * В каждой хромосоме определяются две точки оператора кроссинговера,
     * хромосомы обмениваются участками, расположенными между двумя точками оператора кроссинговера.
     * Точки оператора кроссинговера в двухточечном операторе кроссинговера также определяются случайно.
     * @param parent1 - первый родитель
     * @param parent2 - второй родитель
     * @return individiduals - результат скрещивания (два потомка).
     */
    public static Individidual[] crossoverByTwoPoints(Individidual parent1, Individidual parent2){
        int countOfSigns = parent1.getNumberOfSigns(); // количество признаков в особи

        // точка разреза, после которой выполняется разрез хромосомы (получаем рандомно от 1 до последнего признака)
        int idOfCuttingPoint1 = WorkWithNumbers.getRandomFromTo(1, countOfSigns);
        int idOfCuttingPoint2 = WorkWithNumbers.getRandomFromTo(1, countOfSigns);

        // определяем левую (меньшую) точку
        int leftPoint, rightPoint;
        if (idOfCuttingPoint1 < idOfCuttingPoint2){
            leftPoint = idOfCuttingPoint1;
            rightPoint = idOfCuttingPoint2;
        } else if (idOfCuttingPoint1 == idOfCuttingPoint2){
            leftPoint = idOfCuttingPoint1 - 1;
            rightPoint = idOfCuttingPoint2;
        } else {
            leftPoint = idOfCuttingPoint2;
            rightPoint = idOfCuttingPoint1;
        }

        // создаем два потомка
        Individidual child1 = new Individidual(countOfSigns);
        Individidual child2 = new Individidual(countOfSigns);

        // до точки разрыва признаки оставляем те же
        for (int i = 0; i < leftPoint; i++){
            child1.signs[i] = parent1.signs[i];
            child2.signs[i] = parent2.signs[i];
        }

        // После точки разрыва до следующей точки признаки меняем
        for (int i = leftPoint; i < rightPoint; i++){
            child1.signs[i] = parent2.signs[i];
            child2.signs[i] = parent1.signs[i];
        }

        // После правой точки разрыва признаки оставляем те же
        for (int i = rightPoint; i < countOfSigns; i++){
            child1.signs[i] = parent1.signs[i];
            child2.signs[i] = parent2.signs[i];
        }

        // Записываем детей в массив и возвращаем
        return new Individidual[]{child1, child2};
    }

    /**
     * Мутация особи при помощи прибавления к признаку числа, т.е.
     * увеличиваем/уменьшаем признак на шаг step, чтобы приблизить его к искомой функции.
     * @param fitnessFunc - искомая функция (с ней мы сравниваем признаки)
     */
    public void mutateInStepIncrease(double[] fitnessFunc) {
        // для каждого признака особи:
        for (int i = 0; i < numberOfSigns; ++i) {
            double step = getStep(signs[i], fitnessFunc[i]); // вычисляем шаг, который будем прибавлять к признаку
            signs[i] += step; // увеличиваем признак на найденный шаг
            signs[i] = WorkWithNumbers.round(signs[i], 2); // округляем
        }
    }

    /**
     * Функция для вычисления числа (шага), на которое нужно увеличить признак, чтобы приблизить его к искомой функции.
     * Здесь определяем, в какую сторону будем двигать точку (признак) - вверх или вниз.
     * Если step отрицательный, то будем двигать вниз (уменьшать значение признака)
     * Если step положительный, то будем двигать вверх (увеличивать значение признака)
     * @param gen - признак, который будем сравнивать с точкой искомой функции
     * @param fitness - точка искомой функции
     * @return step - число, которое будем прибавлять к признаку для того, чтобы приблизить его к точке искомой функции
     */
    public double getStep(double gen, double fitness){
        double euclid = WorkWithNumbers.getEuclideanDistance(gen, fitness);// эвклидово расст между признаками и искомой функцией
        double step; // число, которое будем прибавлять к признаку для того, чтобы приблизить его к точке искомой функции

        // сначала ставим 00.1, чтобы проверить: уменьшится ли эвклидово расстояние после того, как признак увеличится на это число
        // это нужно для того, чтобы понять - в какую сторону изменять признак (уменьшать или увеличивать)
        step = 0.01;

        // считаем эвклид расст между точкой искомой функции и признаком, увеличенным на шаг step
        double euclidWithStep = WorkWithNumbers.getEuclideanDistance(gen + step, fitness);

        // сравниваем эвклидовые расстояние до и после увеличения на step
        if (euclidWithStep < euclid){ // если увеличенное значение меньше исходного, тогда будем прибавлять (идем вверх по оси У)
            step = 0.01;
        } else if (euclid == euclidWithStep){  // если равны, то оставляем шаг 0, так как изменений нет
            step = 0;
        } else { // если увеличенное значение меньше исходного, тогда будем уменьшать (идем вниз по оси У)
            step = -0.01;
        }
        return step;
    }

    public int getNumberOfSigns() {
        return numberOfSigns;
    }

    public double[] getSigns() {
        return signs;
    }

    /**
     * Считаем эвклидово расстояние между признаками и искомой функцией
     * @param requiredFunction - искомая функция
     */
    public void countEuclidDistanceForSignsWithRequiredFunc(double[] requiredFunction) {
        this.euclidDist = WorkWithNumbers.getEuclideanDistance(this.signs, requiredFunction);
    }

    @Override
    public String toString() {
        return "Individidual{" +
                "signs=" + Arrays.toString(signs) +
                ", euclidDist=" + euclidDist +
                '}';
    }

    public double getEuclidDist() {
        return WorkWithNumbers.round(euclidDist, 10);
    }

    /**
     * переопределенная функция от Comparable.
     * нужна для того, чтобы сравнивать две особи между собой. Сравниваем особи по значению эвклидова расстояния
     * @param ind - особь, с которой сравниваем
     * @return 1 - больше,
     *         -1 - меньше,
     *         0 - равны
     */
    @Override
    public int compareTo(Individidual ind) {
        BigDecimal bd1 = BigDecimal.valueOf(this.getEuclidDist());
        BigDecimal bd2 = BigDecimal.valueOf(ind.getEuclidDist());
        return bd1.compareTo(bd2);
    }
}
