package es.unex.cum.sei.p1y2.math;

import java.util.Arrays;

public class Matrix {
    private final int[][] data;
    private final int numRows;
    private final int numCols;

    public Matrix(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.data = new int[numRows][numCols];
    }

    public Matrix(int[][] data) {
        this.numRows = data.length;
        this.numCols = data[0].length;
        this.data = data;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getElement(int row, int col) {
        return data[row][col];
    }

    public void setElement(int row, int col, int value) {
        data[row][col] = value;
    }

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

    private int mod(int a, int modulus) {
        int result = a % modulus;
        return (result >= 0) ? result : result + modulus;
    }

    private int modInverse(int a, int modulus) throws ArithmeticException {
        for (int x = 1; x < modulus; x++) {
            if ((a * x) % modulus == 1) {
                return x;
            }
        }
        throw new ArithmeticException("La inversa modular no existe.");

    }

    private void scalarMultiply(int scalar) {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                data[i][j] *= scalar;
            }
        }
    }

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

    private Matrix transpose() {
        int[][] resultData = new int[numCols][numRows];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                resultData[j][i] = data[i][j];
            }
        }

        return new Matrix(resultData);
    }

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < numRows; i++) {
            builder.append(Arrays.toString(data[i])).append('\n');
        }
        return builder.toString();
    }

}
