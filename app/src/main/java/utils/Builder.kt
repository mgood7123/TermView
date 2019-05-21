package utils

import android.util.Log
import android.view.View
import android.widget.AbsoluteLayout
import utils.`class`.extensions.isGreaterThanOrEqualTo
import utils.`class`.extensions.isLessThan
import utils.`class`.extensions.isNotEqualTo
import java.lang.NullPointerException

/**
 * constructs a new [View] that fits the specified **maxHeight** and **maxWidth**
 *
 * a Builder does not contain any [rows][row] when created to avoid confusing between the user adding a [row] 
 * thinking the Builder contains no [rows][row], and then ending up with 1 extra [row], and vice versa (in which 
 * case [column] will throw [ArrayIndexOutOfBoundsException])
 * 
 */
class Builder(val maxHeight: Int, val maxWidth: Int) {
    private class Column(val view: View) {
        var sizeFromTop = 0
        var sizeFromLeft = 0
        var distanceFromLeft = 0
        var distanceFromTop = 0
    }

    private class Row() {
        val column = mutableListOf<Column>()
    }

    private val row = mutableListOf<Row>()

    /**
     * appends a new column to the specified row index
     * 
     * throws [ArrayIndexOutOfBoundsException] if the row is 
     * [empty][MutableList.isEmpty] or [rowIndex] is greater than or equal to 
     * [row.size][MutableList.size]
     *
     * @param rowIndex the row number
     * @param view the [view][View] to attach to this column
     */
    fun column(rowIndex: Int, view: View) {
        if (row.isEmpty() && row.size isGreaterThanOrEqualTo rowIndex) throw ArrayIndexOutOfBoundsException()
        else {
            val c = Column(view)
            c.sizeFromTop = maxHeight
            c.sizeFromLeft = maxWidth
            row[rowIndex].column.add(c)
        }
    }

    /**
     * appends a new column to the specified row index
     * 
     * this version takes a function instead of a direct [View]
     * 
     * this is useful if the same [View] is intended to be used in multiple [columns][column], allowing you to avoid
     * rebuilding the [View] for each [column] manually, eliminating duplicate code
     * 
     * since a [View] cannot have more than one [parent][View.getParent], it needs to be reconstructed for each view 
     * that it wants to be attached to
     *
     * throws [ArrayIndexOutOfBoundsException] if the row is
     * [empty][MutableList.isEmpty] or [rowIndex] is greater than or equal to
     * [row.size][MutableList.size]
     * 
     * @param rowIndex the row number
     * @param view a function that returns a [View]
     */
    fun column(rowIndex: Int, view: () -> View) = column(rowIndex, view())

    /**
     * adds a new row to the [Builder]
     * 
     */
    fun row() {
        row.add(Row())
    }

    /**
     * adds a new row to the [Builder]
     *
     * this version takes a function instead of a direct [View]
     *
     * this is useful if the same [View] is intended to be used in multiple [columns][column], allowing you to avoid
     * rebuilding the [View] for each [column] manually, eliminating duplicate code
     *
     * since a [View] cannot have more than one [parent][View.getParent], it needs to be reconstructed for each view
     * that it wants to be attached to
     *
     */
    fun row(columns: Int, v: () -> View) {
        val i = row.size
        row()
        (1..columns).forEach {
            column(i, v)
        }
    }

    private fun resize() {
        var nextRowOffset = 0
        row.forEach { ROW ->
            var nextOffset = 0
            ROW.column.forEach { COLUMN ->
                COLUMN.sizeFromLeft = COLUMN.sizeFromLeft / ROW.column.size
                COLUMN.distanceFromLeft = nextOffset

                nextOffset += COLUMN.sizeFromLeft

                COLUMN.sizeFromTop = COLUMN.sizeFromTop / row.size
                COLUMN.distanceFromTop = nextRowOffset
            }
            nextRowOffset += maxHeight / row.size
        }
    }

    /**
     * builds the [View] that has been constructed by [row] or [column] and attach's it to [view]
     * 
     * [View] must be an [AbsoluteLayout]
     */
    fun build(view: AbsoluteLayout) {
        resize()
        row.forEach {
            it.column.forEach {
                view.addView(
                    it.view, AbsoluteLayout.LayoutParams(
                        it.sizeFromLeft,
                        it.sizeFromTop,
                        it.distanceFromLeft,
                        it.distanceFromTop
                    )
                )
            }
        }
    }

    /**
     * cells to grid converter
     */
    class ConvertCellsToValidGrid() {

        /**
         * the **Rows** class holds this rows current columns and leftover columns
         */
        inner class Rows {
            var columns = 0
            var leftover = 0
        }

        private fun convertCellsToValidGrid(cells: Int, modulator: Int = 2): MutableList<Rows>? {

            // create an array of rows
            val row = mutableListOf<Rows>()
            // add one row
            row.add(Rows())
            // set the current row index to zero (0)
            var currentRow = 0

            val GRID = true
            // TODO: add orientation conversions for landscape grid (1x2) and portrait grid (2x1), default is landscape
            if (GRID) {
                // create an array of integers with an array size equal to cells, these are unused
                // equivalent to the C for loop: for (int i = 0; i < cells; i++)
                arrayOfNulls<Int>(cells).forEach {

                    val leftoverIsNotInitializedToOne = row[currentRow].leftover != 0

                    val remainderOfLeftoverDividedByModulatorIsEqualToZero =
                        (row[currentRow].leftover % modulator) == 0

                    // if leftover is initialized to 1 it will offset the entire grid by 1 producing incorrect layouts
                    // due to the number of cells being off by 1, make sure this is not true
                    if (leftoverIsNotInitializedToOne) {
                        // if the remainder of dividing the leftover by the modulator is equal to zero (0)
                        if (remainderOfLeftoverDividedByModulatorIsEqualToZero) {
                            // wrap leftover into next row
                            row[currentRow].columns = row[currentRow].leftover
                            row[currentRow].leftover = 0
                            row.add(Rows())
                            currentRow++
                            row[currentRow].leftover = 1
                        }
                    }
                    // if neither of the above conditions are true
                    if (!leftoverIsNotInitializedToOne || !remainderOfLeftoverDividedByModulatorIsEqualToZero) {
                        row[currentRow].leftover++
                    }
                }

                // after the counting we can still end up with leftovers, turn these into columns
                row[currentRow].columns = row[currentRow].leftover
                row[currentRow].leftover = 0

                Log.e("convertCellsToValidGrid", "requested: $cells")
                // check if the grid can be compacted, for example: 5x2 to 3x3
                val sqrt = if (row[0].columns == 0) Math.sqrt(cells.toDouble())
                else Math.sqrt(((currentRow + 1) * row[0].columns).toDouble())
                val rounded = Math.floor(sqrt).toInt()
                Log.e("convertCellsToValidGrid", "requested: $cells : sqrt             :  $sqrt")
                Log.e("convertCellsToValidGrid", "requested: $cells : rounded          :  $rounded")
                Log.e("convertCellsToValidGrid", "requested: $cells : total rows       :  ${currentRow + 1}")
                // if rounded is not equal to modulator and rounded is not equal to 1
                if (rounded != modulator && rounded != 1) {
                    // the grid can be compacted
                    Log.e("convertCellsToValidGrid", "requested: $cells : converting to ${rounded}x$rounded")
                    return convertCellsToValidGrid(cells, rounded)
                } else {
                    // the grid cannot be compacted
                    // print the grid's contents in case anything happens to be incorrect
                    for ((index, it) in row.withIndex()) {
                        Log.e("convertCellsToValidGrid", "requested: $cells : row $index : columns  :  ${it.columns}")
                    }
                    return row
                }
            }
            return null
        }

        /**
         * converts the specified number of **cells** into a grid layout template
         */
        fun convertCellsToValidGrid(cells: Int) = convertCellsToValidGrid(cells, 2)
    }
}