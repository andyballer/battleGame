/**
 * Reads the state from an Xbox 360 controller using JInput. This works on all platforms. On Windows DirectInput will be used,
 * which has the limitation that the left and right triggers share the same axis.
 */
public class JInputXboxController extends XboxController {
        static final int DPAD_UP = 4, DPAD_DOWN = 8, DPAD_LEFT = 16, DPAD_RIGHT = 32;

        private final Controller controller;
        private int dpadDirection;

        JInputXboxController (Controller controller) {
                if (controller == null) throw new IllegalArgumentException("controller cannot be null.");
                this.controller = controller;
        }

        public boolean get (Button button) {
                if (button == null) throw new IllegalArgumentException("button cannot be null.");
                if (!poll()) return false;
                Identifier.Button id = null;
                switch (button) {
                case up:
                        return (dpadDirection & DPAD_UP) == DPAD_UP;
                case down:
                        return (dpadDirection & DPAD_DOWN) == DPAD_DOWN;
                case left:
                        return (dpadDirection & DPAD_LEFT) == DPAD_LEFT;
                case right:
                        return (dpadDirection & DPAD_RIGHT) == DPAD_RIGHT;
                case start:
                case guide:
                        // The Xbox controller driver doesn't expose these buttons!
                        return false;
                case a:
                        id = Identifier.Button._0;
                        break;
                case b:
                        id = Identifier.Button._1;
                        break;
                case x:
                        id = Identifier.Button._2;
                        break;
                case y:
                        id = Identifier.Button._3;
                        break;
                case leftShoulder:
                        id = Identifier.Button._4;
                        break;
                case rightShoulder:
                        id = Identifier.Button._5;
                        break;
                case back:
                        id = Identifier.Button._6;
                        break;
                case leftStick:
                        id = Identifier.Button._8;
                        break;
                case rightStick:
                        id = Identifier.Button._9;
                        break;
                }
                return controller.getComponent(id).getPollData() != 0;
        }

        public float get (Axis axis) {
                if (axis == null) throw new IllegalArgumentException("axis cannot be null.");
                if (!poll()) return 0;
                Identifier.Axis id = null;
                boolean invert = false;
                switch (axis) {
                case leftTrigger:
                case rightTrigger:
                        float value = controller.getComponent(Identifier.Axis.Z).getPollData();
                        if (value > 0)
                                value = axis == Axis.leftTrigger ? value : 0;
                        else
                                value = axis == Axis.rightTrigger ? -value : 0;
                        return value;
                case leftStickX:
                        id = Identifier.Axis.X;
                        break;
                case leftStickY:
                        id = Identifier.Axis.Y;
                        invert = true;
                        break;
                case rightStickX:
                        id = Identifier.Axis.RX;
                        break;
                case rightStickY:
                        id = Identifier.Axis.RY;
                        invert = true;
                        break;
                }
                float value = controller.getComponent(id).getPollData();
                if (invert) value = -value;
                return value;
        }

        public boolean poll () {
                if (!controller.poll()) {
                        notifyDisconnected();
                        return false;
                }
                EventQueue eventQueue = controller.getEventQueue();
                Event event = new Event();
                while (eventQueue.getNextEvent(event)) {
                        Identifier id = event.getComponent().getIdentifier();
                        float value = event.getValue();

                        if (id instanceof Identifier.Button) {
                                notifyListeners(getButton((Identifier.Button)id), value != 0);
                                continue;
                        }

                        if (id == Identifier.Axis.POV) {
                                int newDirection = 0;
                                if (value == Component.POV.RIGHT || value == Component.POV.UP_RIGHT || value == Component.POV.DOWN_RIGHT)
                                        newDirection |= DPAD_RIGHT;
                                if (value == Component.POV.LEFT || value == Component.POV.UP_LEFT || value == Component.POV.DOWN_LEFT)
                                        newDirection |= DPAD_LEFT;
                                if (value == Component.POV.DOWN || value == Component.POV.DOWN_LEFT || value == Component.POV.DOWN_RIGHT)
                                        newDirection |= DPAD_DOWN;
                                if (value == Component.POV.UP || value == Component.POV.UP_LEFT || value == Component.POV.UP_RIGHT)
                                        newDirection |= DPAD_UP;
                                // If the direction has changed, press or release the dpad buttons.
                                int diff = dpadDirection ^ newDirection;
                                if ((diff & DPAD_RIGHT) != 0) notifyListeners(Button.right, (newDirection & DPAD_RIGHT) == DPAD_RIGHT);
                                if ((diff & DPAD_LEFT) != 0) notifyListeners(Button.left, (newDirection & DPAD_LEFT) == DPAD_LEFT);
                                if ((diff & DPAD_DOWN) != 0) notifyListeners(Button.down, (newDirection & DPAD_DOWN) == DPAD_DOWN);
                                if ((diff & DPAD_UP) != 0) notifyListeners(Button.up, (newDirection & DPAD_UP) == DPAD_UP);
                                dpadDirection = newDirection;
                                continue;
                        }

                        if (id instanceof Identifier.Axis) {
                                Axis axis = null;
                                if (id == Identifier.Axis.X) axis = Axis.leftStickX;
                                if (id == Identifier.Axis.Y) axis = Axis.leftStickY;
                                if (id == Identifier.Axis.RX) axis = Axis.rightStickX;
                                if (id == Identifier.Axis.RY) axis = Axis.rightStickY;
                                if (id == Identifier.Axis.Z) axis = value < 0 ? Axis.leftTrigger : Axis.rightTrigger;
                                if (axis != null) {
                                        notifyListeners(axis, value);
                                        continue;
                                }
                        }
                }
                return true;
        }

        private Button getButton (Identifier.Button id) {
                if (id == Identifier.Button._0) return Button.a;
                if (id == Identifier.Button._1) return Button.b;
                if (id == Identifier.Button._2) return Button.x;
                if (id == Identifier.Button._3) return Button.y;
                if (id == Identifier.Button._4) return Button.leftShoulder;
                if (id == Identifier.Button._5) return Button.rightShoulder;
                if (id == Identifier.Button._6) return Button.back;
                if (id == Identifier.Button._8) return Button.leftStick;
                if (id == Identifier.Button._9) return Button.rightStick;
                throw new IllegalArgumentException("Unknown button ID: " + id);
        }

        public int getPort () {
                return controller.getPortNumber() + 1;
        }

        public ControllerInput getLastInput () {
                if (lastButton != null) return new ControllerInput(controller.getName(), lastButton);
                if (lastAxis != null) return new ControllerInput(controller.getName(), lastAxis);
                return null;
        }

        public int hashCode () {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((controller == null) ? 0 : controller.hashCode());
                return result;
        }

        public boolean equals (Object obj) {
                if (this == obj) return true;
                if (obj == null) return false;
                if (getClass() != obj.getClass()) return false;
                JInputXboxController other = (JInputXboxController)obj;
                if (controller == null) {
                        if (other.controller != null) return false;
                } else if (!controller.equals(other.controller)) return false;
                return true;
        }

        static public class ControllerInput implements Input {
                private Button button;
                private Axis axis;
                private String controllerName;
                private transient JInputXboxController device;

                public ControllerInput (String controllerName, Button button) {
                        this.controllerName = controllerName;
                        this.button = button;
                }

                public ControllerInput (String controllerName, Axis axis) {
                        this.controllerName = controllerName;
                        this.axis = axis;
                }

                public float getState () {
                        if (device == null) {
                                getInputDevice();
                                if (device == null) return 0;
                        }
                        if (axis != null) return device.get(axis);
                        if (button != null) return device.get(button) ? 1 : 0;
                        return 0;
                }

                public float getOtherState () {
                        switch (axis) {
                        case leftStickX:
                                return device.get(Axis.leftStickY);
                        case leftStickY:
                                return device.get(Axis.leftStickX);
                        case rightStickX:
                                return device.get(Axis.rightStickY);
                        case rightStickY:
                                return device.get(Axis.rightStickX);
                        default:
                                return 0;
                        }
                }

                public JInputXboxController getInputDevice () {
                        if (device != null) return device;
                        for (Controller controller : ControllerEnvironment.getDefaultEnvironment().getControllers()) {
                                if (!controller.getName().equals(controllerName)) continue;
                                device = new JInputXboxController(controller);
                                return device;
                        }
                        return null;
                }

                public boolean isValid () {
                        return getInputDevice() != null;
                }

                public boolean isAxis () {
                        return axis != null && !axis.isTrigger();
                }

                public boolean isAxisX () {
                        return axis == Axis.leftStickX || axis == Axis.rightStickX;
                }

                public String toString () {
                        if (button != null) return button + " button";
                        if (axis != null) return axis + " axis";
                        return "<none>";
                }
        }

        static public List<XboxController> getJInputControllers () {
                ArrayList<XboxController> list = new ArrayList();
                for (Controller controller : ControllerEnvironment.getDefaultEnvironment().getControllers())
                        if (controller.getType() == Controller.Type.GAMEPAD) list.add(new JInputXboxController(controller));
                return list;
        }
}