import greenfoot.*;

/**
 * Element - an ENUM (enumeration).
 *
 * An enum is a special class that has a FIXED list of possible values.
 * Here the only possible values are AIR, SURYA and BAYANGAN, so Java will never
 * let us accidentally create a "fourth" element by typo (unlike using Strings).
 *
 * Each value also carries DATA (a display name and a colour), which we pass to
 * the enum's constructor. Enums can have fields, constructors and methods just
 * like normal classes.
 */
public enum Element {

    // Each line below calls the constructor further down.
    AIR("Glif Air", new Color(70, 160, 255)),
    SURYA("Glif Surya", new Color(255, 200, 50)),
    BAYANGAN("Glif Bayangan", new Color(160, 90, 210));

    // 'final' = the value can never change after the constructor sets it.
    private final String displayName;
    private final Color color;

    // Enum constructors are always private (implicitly); only the lines above can call it.
    Element(String displayName, Color color) {
        this.displayName = displayName;   // 'this.' means "the field of THIS object"
        this.color = color;
    }

    // Getters: the fields are private (ENCAPSULATION), so other classes read them via methods.
    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }
}
