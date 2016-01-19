/**
 * An input device for an Xbox 360 controller.
 */
public abstract class XboxController implements InputDevice {
        private Listeners<Listener> listeners = new Listeners(Listener.class);

        protected Button lastButton;
        protected Axis lastAxis;

        /**
         * Returns the button state.
         */
        abstract public boolean get (Button button);

        /**
         * Returns the axis state.
         */
        abstract public float get (Axis axis);

        abstract public int getPort ();

        /**
         * Returns the button or axis state.
         */
        public float get (Target target) {
                if (target == null) throw new IllegalArgumentException("target cannot be null.");
                if (target instanceof Button)
                        return get((Button)target) ? 1 : 0;
                else if (target instanceof Axis)
                        return get((Axis)target);
                else
                        throw new IllegalArgumentException("target must be a button or axis.");
        }

        /**
         * Returns the button or axis state.
         */
        public float get (String target) {
                return get(Device.getTarget(target));
        }

        /**
         * Adds a listener to be notified when any buttons or axes change state.
         */
        public void addListener (Listener listener) {
                listeners.addListener(listener);
        }

        public void removeListener (Listener listener) {
                listeners.removeListener(listener);
        }

        protected void notifyListeners (Button button, boolean pressed) {
                if (pressed) lastButton = button;
                Listener[] listeners = this.listeners.toArray();
                for (int i = 0, n = listeners.length; i < n; i++)
                        listeners[i].buttonChanged(button, pressed);
        }

        protected void notifyListeners (Axis axis, float state) {
                lastAxis = axis;
                Listener[] listeners = this.listeners.toArray();
                for (int i = 0, n = listeners.length; i < n; i++)
                        listeners[i].axisChanged(axis, state);
        }

        protected void notifyDisconnected () {
                Listener[] listeners = this.listeners.toArray();
                for (int i = 0, n = listeners.length; i < n; i++)
                        listeners[i].disconnected();
        }

        protected void notifyConnected () {
                Listener[] listeners = this.listeners.toArray();
                for (int i = 0, n = listeners.length; i < n; i++)
                        listeners[i].connected();
        }

        public boolean resetLastInput () {
                if (!poll()) return false;
                lastButton = null;
                lastAxis = null;
                return true;
        }

        public String getName () {
                return "Xbox Controller (" + getPort() + ")";
        }

        public String toString () {
                return getName();
        }

        /**
         * Returns a list of all connected XboxControllers. On operating systems other than Windows, the list may include devices other
         * than Xbox 360 controllers.
         */
        static public List<XboxController> getAll () {
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                        ArrayList<XboxController> list = new ArrayList();
                        for (XInputXboxController controller : XInputXboxController.getXInputControllers())
                                list.add(controller);
                        return list;
                }
                return JInputXboxController.getJInputControllers();
        }

        /**
         * Listener to be notified when the controller's buttons or axes change state.
         */
        static public class Listener {
                public void connected () {
                }

                public void disconnected () {
                }

                public void buttonChanged (Button button, boolean pressed) {
                }

                public void axisChanged (Axis axis, float state) {
                }
        }
}
