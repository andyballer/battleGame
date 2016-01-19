/**
 * Represents a device button.
 */
public enum Button implements Target {
        // Ordinals defined by firmware.
        a("A", null), //
        b("B", null), //
        x("X", null), //
        y("Y", null), //
        up("Up", "U"), //
        down("Down", "D"), //
        left("Left", "L"), //
        right("Right", "R"), //
        leftShoulder("Left Shoulder", "LSH"), //
        rightShoulder("Right Shoulder", "RSH"), //
        leftStick("Left Stick", "LST"), //
        rightStick("Right Stick", "RST"), //
        start("Start", "S"), //
        guide("Guide", "G"), //
        back("Back", "BK");

        private final String friendlyName;
        private final String alias;

        private Button (String friendlyName, String alias) {
                this.friendlyName = friendlyName + " button";
                this.alias = alias;
        }

        public String getAlias () {
                return alias;
        }

        public String toString () {
                return friendlyName;
        }
}