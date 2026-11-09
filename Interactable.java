/**
 * Interactable - an INTERFACE.
 *
 * An interface is a promise: "any class that implements me MUST have these methods".
 * It says WHAT can be done, not HOW. Npc and DomainGate are completely different
 * classes, but both implement Interactable, so the Player can treat them the same way:
 *     "find the closest Interactable, show its prompt, call interact() when E is pressed".
 *
 * Want a treasure chest, a sign or a shop? Write a class that implements Interactable
 * and the Player will already know how to use it.
 */
public interface Interactable {

    /** Text shown at the bottom of the screen when the player is close, e.g. "E: bicara". */
    String getPrompt();

    /** What happens when the player presses E next to it. */
    void interact(MyWorld world);

    // The two methods below are already written in MapActor, so a class that extends
    // MapActor automatically keeps this part of the promise.
    double getMapX();

    double getMapY();
}
