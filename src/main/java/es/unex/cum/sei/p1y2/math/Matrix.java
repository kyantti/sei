package es.unex.cum.sei.p1y2.math;

import java.util.Arrays;

public class Matrix {
    private final int[][] data;
    private final int numRows;
    private final int numCols;
    /**
     * Crea una matriz con el número especificado de filas y columnas, inicializadas a cero.
     *
     * @param numRows El número de filas de la matriz.
     * @param numCols El número de columnas de la matriz.
     */
    public Matrix(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.data = new int[numRows][numCols];
    }
    /**
     * Crea una matriz a partir de una matriz de datos existente.
     *
     * @param data La matriz de datos para inicializar esta matriz.
     */
    public Matrix(int[][] data) {
        this.numRows = data.length;
        this.numCols = data[0].length;
        this.data = data;
    }
    /**
     * Obtiene el número de filas de la matriz.
     *
     * @return El número de filas de la matriz.
     */
    public int getNumRows() {
        return numRows;
    }
    /**
     * Obtiene el número de columnas de la matriz.
     *
     * @return El número de columnas de la matriz.
     */
    public int getNumCols() {
        return numCols;
    }
    /**
     * Obtiene el elemento en una posición específica de la matriz.
     *
     * @param row La fila del elemento.
     * @param col La columna del elemento.
     * @return El valor del elemento en la posición especificada.
     */
    public int getElement(int row, int col) {
        return data[row][col];
    }
    /**
     * Establece el valor de un elemento en una posición específica de la matriz.
     *
     * @param row   La fila del elemento.
     * @param col   La columna del elemento.
     * @param value El nuevo valor del elemento.
     */
    public void setElement(int row, int col, int value) {
        data[row][col] = value;
    }
    /**
     * Multiplica esta matriz por otra matriz.
     *
     * @param other La otra matriz con la que multiplicar.
     * @return El resultado de la multiplicación de matrices.
     * @throws IllegalArgumentException Si las dimensiones de las matrices no son compatibles para la multiplicación.
     */
    public Matrix multiply(Matrix other) throws IllegalArgumentException {
        if (numCols != other.getNumRows()) {
            throw new IllegalArgumentException("Las dimensiones de las matrices no son compatibles para su multiplicacion.");
        }

        int resultRows = numRows;
        int resultCols = other.getNumCols();
        int[][] resultData = new int[resultRows][resultCols];

        for (int i = 0; i < resultRows; i++) {
            for (int j = 0; j < resultCols; j++) {
                int sum = 0;
                for (int k = 0; k < numCols; k++) {
                    sum += data[i][k] * other.getElement(k, j);
                }
                resultData[i][j] = sum;
            }
        }

        return new Matrix(resultData);
    }
    /**
     * Multiplica esta matriz por otra matriz con un módulo especificado.
     *
     * @param other   La otra matriz con la que multiplicar.
     * @param modulus El módulo para aplicar a los cálculos.
     * @return El resultado de la multiplicación de matrices con módulo.
     * @throws IllegalArgumentException Si las dimensiones de las matrices no son compatibles para la multiplicación.
     */
    public Matrix modularMultiply(Matrix other, int modulus) throws IllegalArgumentException {
        if (numCols != other.getNumRows()) {
            throw new IllegalArgumentException("Las dimensiones de las matrices no son compatibles para su multiplicacion.");
        }

        int resultRows = numRows;
        int resultCols = other.getNumCols();
        int[][] resultData = new int[resultRows][resultCols];

        for (int i = 0; i < resultRows; i++) {
            for (int j = 0; j < resultCols; j++) {
                int sum = 0;
                for (int k = 0; k < numCols; k++) {
                    sum += data[i][k] * other.getElement(k, j);
                }
                resultData[i][j] = mod(sum, modulus); // Apply modulus to the result
            }
        }

        return new Matrix(resultData);
    }
    /**
     * Calcula el determinante de la matriz.
     *
     * @return El determinante de la matriz.
     * @throws IllegalArgumentException Si la matriz no es cuadrada.
     */
    public int determinant() throws IllegalArgumentException {
        if (numRows != numCols) {
            throw new IllegalArgumentException("La matriz debe ser cuadrada para poder calcular su determinante.");
        }

        if (numRows == 1) {
            return data[0][0];
        }
        else if (numRows == 2) {
            return data[0][0] * data[1][1] - data[0][1] * data[1][0];
        }
        else {
            int det = 0;
            for (int i = 0; i < numRows; i++) {
                det += data[0][i] * getCofactor(0, i).determinant() * ((i % 2 == 0) ? 1 : -1);
            }
            return det;
        }
    }
    /**
     * Obtiene el cofactor de la matriz eliminando una fila y una columna específicas.
     *
     * @param row La fila a eliminar.
     * @param col La columna a eliminar.
     * @return El cofactor de la matriz.
     */
    public Matrix getCofactor(int row, int col) {
        int[][] cofactorData = new int[numRows - 1][numCols - 1];
        int rowIndex = 0;
        for (int i = 0; i < numRows; i++) {
            if (i == row) {
                continue;
            }
            int colIndex = 0;
            for (int j = 0; j < numCols; j++) {
                if (j == col) {
                    continue;
                }
                cofactorData[rowIndex][colIndex] = data[i][j];
                colIndex++;
            }
            rowIndex++;
        }
        return new Matrix(cofactorData);
    }
    /**
     * Calcula la inversa modular de la matriz con respecto a un módulo especificado.
     *
     * @param modulus El módulo para aplicar a los cálculos.
     * @return La matriz inversa modular.
     * @throws ArithmeticException Si la matriz no es invertible.
     */
    public Matrix modularInverse(int modulus) throws ArithmeticException {
        int det = determinant();
        int detMod = mod(det, modulus);

        if (detMod == 0) {
            throw new ArithmeticException("La matriz no es invertible, el determinante es cero modulo" + modulus);
        }

        int detInverseMod = modInverse(detMod, modulus);

        Matrix adjoint = getAdjoint();
        adjoint.scalarMultiply(detInverseMod);
        adjoint = adjoint.mod(modulus);

        return adjoint;
    }
    /**
     * Calcula el resultado de a módulo modulus, asegurándose de que el resultado esté en el rango [0, modulus).
     *
     * @param a       El número a calcular módulo.
     * @param modulus El módulo para aplicar al cálculo.
     * @return El resultado de a módulo modulus.
     */
    private int mod(int a, int modulus) {
        int result = a % modulus;
        return (result >= 0) ? result : result + modulus;
    }
    /**
     * Calcula el inverso modular de a en el módulo especificado.
     *
     * @param a       El número para el cual se calcula el inverso modular.
     * @param modulus El módulo para aplicar al cálculo.
     * @return El inverso modular de a en el módulo modulus.
     * @throws ArithmeticException Si no existe un inverso modular para a en el módulo modulus.
     */
    private int modInverse(int a, int modulus) throws ArithmeticException {
        for (int x = 1; x < modulus; x++) {
            if ((a * x) % modulus == 1) {
                return x;
            }
        }
        throw new ArithmeticException("La inversa modular no existe.");

    }
    /**
     * Multiplica cada elemento de la matriz por un escalar.
     *
     * @param scalar El escalar por el cual multiplicar todos los elementos de la matriz.
     */
    private void scalarMultiply(int scalar) {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                data[i][j] *= scalar;
            }
        }
    }
    /**
     * Calcula la matriz adjunta (adjugada) de la matriz actual.
     *
     * @return La matriz adjunta de la matriz actual.
     */
    private Matrix getAdjoint() {
        int resultRows = numRows;
        int resultCols = numCols;
        int[][] resultData = new int[resultRows][resultCols];

        for (int i = 0; i < resultRows; i++) {
            for (int j = 0; j < resultCols; j++) {
                Matrix cofactor = getCofactor(i, j);
                int sign = ((i + j) % 2 == 0) ? 1 : -1;
                resultData[i][j] = sign * cofactor.determinant();
            }
        }

        return new Matrix(resultData).transpose();
    }
    /**
     * Transpone la matriz actual (intercambia filas y columnas).
     *
     * @return La matriz transpuesta de la matriz actual.
     */
    private Matrix transpose() {
        int[][] resultData = new int[numCols][numRows];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                resultData[j][i] = data[i][j];
            }
        }

        return new Matrix(resultData);
    }
    /**
     * Calcula una nueva matriz donde cada elemento de la matriz actual se calcula como el módulo del elemento
     * correspondiente de la matriz actual en el módulo especificado.
     *
     * @param modulus El módulo para aplicar a cada elemento de la matriz.
     * @return Una nueva matriz con elementos calculados como el módulo de la matriz actual en el módulo especificado.
     */
    public Matrix mod(int modulus) {
        int resultRows = numRows;
        int resultCols = numCols;
        int[][] resultData = new int[resultRows][resultCols];

        for (int i = 0; i < resultRows; i++) {
            for (int j = 0; j < resultCols; j++) {
                resultData[i][j] = mod(data[i][j], modulus);
            }
        }

        return new Matrix(resultData);
    }
    /**
     * Devuelve una representación de cadena de la matriz.
     *
     * @return La representación de cadena de la matriz.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < numRows; i++) {
            builder.append(Arrays.toString(data[i]));
            if (i < numRows - 1) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

}
