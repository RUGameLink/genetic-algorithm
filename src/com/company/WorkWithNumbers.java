package com.company;

import java.util.Random;

public class WorkWithNumbers {

    /**
     * Получить рандомное число от a до b
     * @param a Начальное значение диапазона - "от"
     * @param b Конечное значение диапазона - "до"
     * @return рандомное число от a до b
     */
    public static int getRandomFromTo(int a, int b){
        return a + (int) (Math.random() * b);
    }

    /**
     * Общая формула евклидова расстояния для n-мерного случая (n переменных)
     * @param x - первая точка
     * @param y - вторая точка
     */
    public static double getEuclideanDistance(double[] x, double[] y){
        double d = 0;

        for (int i = 0; i < x.length; i++){
            d += Math.pow( ( x[i] - y[i] ), 2 );
        }
        d = Math.sqrt(d);

        return d;
    }

    /**
     * Общая формула евклидова расстояния для 2-мерного случая
     * @param x - первая точка
     * @param y - вторая точка
     */
    public static double getEuclideanDistance(double x, double y){
        double d;
        d = Math.pow( (x - y), 2 );
        d = Math.sqrt(d);
        return d;
    }


    /**
     * Создание массива double со случайными числами
     * @param countOfElements - количество элементов, которые нужно сгенерировать
     * @param rangeMin - от этого числа начнется генериция
     * @param rangeMax - до этого числа будем генерировать
     * @return массив чисел
     */
    public static double[] getRandomArray(int countOfElements, double rangeMin, double rangeMax) {
        double[] randomArray = new double[countOfElements];
        Random r = new Random();

        for(int i = 0; i < countOfElements; ++i) {
            randomArray[i] = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            randomArray[i] = round(randomArray[i], 2);
        }

        return randomArray;
    }

    /**
     * Создание массива с заданным шагом от точки start до точки end
     * @param step - шаг
     * @param start - начальная точка
     * @param countOfElements - кол-во элементов, которое нужно сгенерить
     */
    public static double[] getArrayFromTo(double step, double start, int countOfElements){
        double[] result = new double[countOfElements];
        result[0] = start;

        for (int k = 1; k < countOfElements; k++){
            result[k] = result[k - 1] + step; // считаем значение прибавив к нему шаг
            result[k] = round(result[k], 2); // округляем
        }
        return result;
    }

    /**
     * создаем искомую функцию
     * @param x - массив иксов
     * @return массив Y = искомая функция для ГА
     */
    public static double[] getFunction(double[] x){
        // задаем размер массива
        int countOfElements = x.length;
        double[] y = new double[countOfElements];

        // здесь заполняем массив.
        for (int i = 0; i < countOfElements; i++){
            y[i] = Math.cos(x[i]);
            y[i] = round(y[i], 2);
        }

        return y;
    }

    /**
     * Округление
     * @param value число, которое нужно округлить
     * @param precision - до какого знака
     * @return округленное число
     */
    static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }


}
