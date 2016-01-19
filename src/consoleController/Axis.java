/**
 * Represents a device axis.
 */
public enum Axis implements Target {
        // Ordinals defined by firmware.
        leftStickX("Left Stick X", "LX"), //
        leftStickY("Left Stick Y", "LY"), //
        rightStickX("Right Stick X", "RY"), //
        rightStickY("Right Stick Y", "RX"), //
        leftTrigger("Left Trigger", "LT"), //
        rightTrigger("Right Trigger", "RT");

        private final String friendlyName;
        private final String alias;

        private Axis (String friendlyName, String alias) {
                this.friendlyName = friendlyName + " axis";
                this.alias = alias;
        }

        /**
         * Returns the stick this axis belongs to, or null if this axis is a trigger.
         */
        public Stick getStick () {
                switch (this) {
                case leftStickX:
                case leftStickY:
                        return Stick.left;
                case rightStickX:
                case rightStickY:
                        return Stick.right;
                case leftTrigger:
                case rightTrigger:
                        return null;
                default:
                        throw new RuntimeException("Missing stick entry: " + this);
                }
        }

        public boolean isTrigger () {
                return this == leftTrigger || this == rightTrigger;
        }

        public boolean isX () {
                return this == leftStickX || this == rightStickX;
        }

        public boolean isY () {
                return this == leftStickY || this == rightStickY;
        }

        public String getAlias () {
                return alias;
        }

        public String toString () {
                return friendlyName;
        }
}