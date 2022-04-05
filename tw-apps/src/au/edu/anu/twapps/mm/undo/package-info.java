/**
 * <p>
 * Undo/Redo system for ModelMaker.
 * </p>
 * <p>
 * The state of ModelMaker is saved just before an edit in enacted. The state is
 * saved in three sequentially numbered files: the configuration graph
 * (stateA{@literal <n>}.utg), the layout graph (stateB{@literal <n>}.utg) and
 * the ModelMaker preferences (stateA{@literal <n>}.xml) to capture the current
 * state of user interface controls).
 * </p>
 * <p>
 * All files are deleted when a project is closed.
 * </p>
 * <img src="{@docRoot}/../doc/images/memento.svg" width="500"
 * alt="Memento pattern"/>
 *
 * 
 * @author Ian Davies - 05 Apl 2022
 */
package au.edu.anu.twapps.mm.undo;