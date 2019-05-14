package gameoflife;

import java.util.Collection;

public interface GridTest {
	/**
     * Gets the status of a cell (alive or dead).
     *
     * @param col x-position.
     * @param row y-position.
     * @return Living or not.
     */
    boolean isAlive(int col, int row);

    /**
     * Sets the status of a cell (alive or dead).
     *
     * @param col x-position.
     * @param row y-position.
     * @param alive Living or not.
     */
    void setAlive(int col, int row, boolean alive);

    /**
     * Resizes the cell grid in x and y direction.
     *
     * @param cols New number of columns.
     * @param rows New number of rows.
     */
    void resize(int cols, int rows);

    /**
     * Gets the dimension of the cell grid in x direction.
     *
     * @return Number of columns.
     */
    int getColumns();

    /**
     * Gets the dimension of the cell grid in y direction.
     *
     * @return Number of rows.
     */
    int getRows();

    /**
     * Gets all living cells.
     *
     * @return Set of all cells which are alive.
     */
    Collection<Cell> getPopulation();

    /**
     * Clears the grid.
     */
    void clear();

    /**
     * Computes the next generation.
     */
    void next();

    /**
     * Gets the number of generations in this game.
     *
     * @return The current generation.
     */
    int getGenerations();

    /**
     * Gets the string representation of the current game state.
     *
     * @return The matrix as string.
     */
    String toString();

}

