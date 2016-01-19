abstract public class Device {
        static private final HashMap<String, Target> nameToTarget = new HashMap();
        static private List<Target> targets = new ArrayList();
        static {
                for (Button button : Button.values()) {
                        targets.add(button);
                        nameToTarget.put(button.name().toLowerCase(), button);
                        String friendlyName = button.toString().toLowerCase();
                        nameToTarget.put(friendlyName, button);
                        nameToTarget.put(friendlyName.substring(0, friendlyName.length() - 7), button);
                        if (button.getAlias() != null) nameToTarget.put(button.getAlias().toLowerCase(), button);
                }
                for (Axis axis : Axis.values()) {
                        targets.add(axis);
                        nameToTarget.put(axis.name().toLowerCase(), axis);
                        String friendlyName = axis.toString().toLowerCase();
                        nameToTarget.put(friendlyName, axis);
                        nameToTarget.put(friendlyName.substring(0, friendlyName.length() - 5), axis);
                        if (axis.getAlias() != null) nameToTarget.put(axis.getAlias().toLowerCase(), axis);
                }
                targets = Collections.unmodifiableList(targets);
        }

        protected Thread collectingChangesThread;

        private boolean[] buttonStates = new boolean[Button.values().length];
        private float[] axisStates = new float[Axis.values().length];
        private float[] axisDeflections = new float[Axis.values().length];
        private boolean[] snapshotButtonStates = new boolean[Button.values().length];
        private float[] snapshotAxisStates = new float[Axis.values().length];

        private final Listeners<Listener> listeners = new Listeners(Listener.class);
        private final Deadzone[] stickToDeadzone = new Deadzone[Stick.values().length];
        private float mouseDeltaX, mouseDeltaY;
        private Stick mouseDeltaStick;
        private Map<String, Target> alternateNameToTarget = new HashMap();

        public Device () {
                super();
        }

        /**
         * Does the actual communication with the device to set the button state.
         * @throws IOException When communication with the device fails.
         */
        abstract protected void setButton (Button button, boolean pressed) throws IOException;

        /**
         * Does the actual communication with the device to set the axis state.
         * @throws IOException When communication with the device fails.
         */
        abstract protected void setAxis (Axis axis, float state) throws IOException;

        /**
         * Sets the button state.
         * @throws IOException When communication with the device fails.
         */
        public void set (Button button, boolean pressed) throws IOException {
                if (button == null) throw new IllegalArgumentException("button cannot be null.");

                synchronized (this) {
                        int ordinal = button.ordinal();
                        if (collectingChangesThread == Thread.currentThread()) {
                                buttonStates[ordinal] = pressed;
                                return;
                        }
                        if (buttonStates[ordinal] == pressed) return;
                        setButton(button, pressed);
                        buttonStates[ordinal] = pressed;
                        snapshotButtonStates[ordinal] = pressed;
                }

                notifyButtonChanged(button, pressed);
        }

        /**
         * Sets the axis state.
         * @throws IOException When communication with the device fails.
         */
        public void set (Axis axis, float state) throws IOException {
                if (axis == null) throw new IllegalArgumentException("axis cannot be null.");

                if (axis.isTrigger()) {
                        if (state < 0)
                                state = 0;
                        else if (state > 1) state = 1;
                } else {
                        if (state < -1)
                                state = -1;
                        else if (state > 1) state = 1;
                }

                synchronized (this) {
                        int ordinal = axis.ordinal();
                        if (collectingChangesThread == Thread.currentThread()) {
                                axisStates[ordinal] = state;
                                return;
                        }
                        if (axisStates[ordinal] == state) return;
                        setAxis(axis, state);
                        axisDeflections[ordinal] = state;
                        axisStates[ordinal] = state;
                        snapshotAxisStates[ordinal] = state;
                }

                notifyAxisChanged(axis, state);
        }

        private void notifyButtonChanged (Button button, boolean pressed) {
                if (DEBUG) debug(button + ": " + pressed);
                Listener[] listeners = this.listeners.toArray();
                for (int i = 0, n = listeners.length; i < n; i++)
                        listeners[i].buttonChanged(button, pressed);
        }

        private void notifyAxisChanged (Axis axis, float state) {
                if (DEBUG) debug(axis + ": " + state);
                Listener[] listeners = this.listeners.toArray();
                for (int i = 0, n = listeners.length; i < n; i++)
                        listeners[i].axisChanged(axis, state);
        }

        /**
         * Sets the button or axis state. If the target is an axis, it will be to 0 (false) or 1 (true).
         * @throws IOException When communication with the device fails.
         */
        public void set (Target target, boolean pressed) throws IOException {
                if (target == null) throw new IllegalArgumentException("target cannot be null.");
                if (target instanceof Button)
                        set((Button)target, pressed);
                else if (target instanceof Axis)
                        set((Axis)target, pressed ? 1 : 0);
                else
                        throw new IllegalArgumentException("target must be a button or axis.");
        }

        /**
         * Sets the button or axis state. If the target is a button, it will be to not pressed (zero) or pressed (nonzero).
         * @throws IOException When communication with the device fails.
         */
        public void set (Target target, float state) throws IOException {
                if (target == null) throw new IllegalArgumentException("target cannot be null.");
                if (target instanceof Button)
                        set((Button)target, state != 0);
                else if (target instanceof Axis)
                        set((Axis)target, state);
                else
                        throw new IllegalArgumentException("target must be a button or axis.");
        }

        /**
         * Sets the button or axis state. If the target is an axis, it will be to 0 (false) or 1 (true).
         * @throws IOException When communication with the device fails.
         */
        public void set (String targetName, boolean pressed) throws IOException {
                if (targetName == null) throw new IllegalArgumentException("targetName cannot be null.");
                String name = targetName.trim().toLowerCase();
                Target target = nameToTarget.get(name);
                if (target == null) {
                        target = alternateNameToTarget.get(name);
                        if (target == null) throw new IllegalArgumentException("Unknown target: " + targetName);
                }
                set(target, pressed);
        }

        /**
         * Sets the button or axis state. If the target is a button, it will be to not pressed (zero) or pressed (nonzero).
         * @throws IOException When communication with the device fails.
         */
        public void set (String targetName, float state) throws IOException {
                if (targetName == null) throw new IllegalArgumentException("targetName cannot be null.");
                String name = targetName.trim().toLowerCase();
                Target target = nameToTarget.get(name);
                if (target == null) {
                        target = alternateNameToTarget.get(name);
                        if (target == null) throw new IllegalArgumentException("Unknown target: " + targetName);
                }
                set(target, state);
        }

        /**
         * Sets the x and y axes for the specified stick.
         * @throws IOException When communication with the device fails.
         */
        public void set (Stick stick, float stateX, float stateY) throws IOException {
                if (stick == null) throw new IllegalArgumentException("stick cannot be null.");
                set(stick.getAxisX(), stateX);
                set(stick.getAxisY(), stateY);
        }

        /**
         * Sets the x and y axes for the specified stick.
         * @throws IOException When communication with the device fails.
         */
        public void set (String stick, float stateX, float stateY) throws IOException {
                if (stick == null) throw new IllegalArgumentException("stick cannot be null.");
                stick = stick.toLowerCase();
                if (stick.equals("leftstick") || stick.equals("left") || stick.equals("l"))
                        set(Stick.left, stateX, stateY);
                else if (stick.equals("rightstick") || stick.equals("right") || stick.equals("r"))
                        set(Stick.right, stateX, stateY);
                else
                        throw new IllegalArgumentException("stick must be leftStick or rightStick.");
        }

        /**
         * Returns the last state of the button as set by the device.
         */
        public boolean get (Button button) {
                if (button == null) throw new IllegalArgumentException("button cannot be null.");
                return buttonStates[button.ordinal()];
        }

        /**
         * Returns the last state of the axis as set by the device.
         */
        public float get (Axis axis) {
                if (axis == null) throw new IllegalArgumentException("axis cannot be null.");
                return axisStates[axis.ordinal()];
        }

        /**
         * Returns the last state of the button or axis as set by the device. If the target is a button, either 0 (not pressed) or 1
         * (pressed) is returned.
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
         * Returns the last state of the button or axis as set by the device. If the target is a button, either 0 (not pressed) or 1
         * (pressed) is returned.
         */
        public float get (String targetName) {
                if (targetName == null) throw new IllegalArgumentException("targetName cannot be null.");
                String name = targetName.trim().toLowerCase();
                Target target = nameToTarget.get(name);
                if (target == null) {
                        target = alternateNameToTarget.get(name);
                        if (target == null) throw new IllegalArgumentException("Unknown target: " + targetName);
                }
                return get(target);
        }

        /**
         * Closes the connection with this device. No further communication will be possible with this device instance.
         */
        abstract public void close ();

        /**
         * Sets all buttons to released and all axes to zero.
         * @throws IOException When communication with the device fails.
         */
        public void reset () throws IOException {
                for (Button button : Button.values())
                        setButton(button, false);
                for (Axis axis : Axis.values())
                        setAxis(axis, 0);
                for (int i = 0, n = buttonStates.length; i < n; i++)
                        buttonStates[i] = false;
                for (int i = 0, n = axisStates.length; i < n; i++) {
                        axisStates[i] = 0;
                        axisDeflections[i] = 0;
                }
                if (collectingChangesThread != null) collect();

                if (DEBUG) debug("Device reset.");
                Listener[] listeners = this.listeners.toArray();
                for (int i = 0, n = listeners.length; i < n; i++)
                        listeners[i].deviceReset();
        }

        /**
         * Sets alternate names for targets.
         * @param names Map from target name to alternate name.
         */
        public void setTargetNames (Map<String, String> names) {
                if (names == null) throw new IllegalArgumentException("names cannot be null.");
                alternateNameToTarget.clear();
                for (Entry<String, String> entry : names.entrySet()) {
                        if (entry.getKey() == null || entry.getKey().length() == 0) continue;
                        alternateNameToTarget.put(entry.getValue().toLowerCase(), getTarget(entry.getKey()));
                }
        }

        public void setDeadzone (Stick stick, Deadzone deadzone) {
                if (stick == null) throw new IllegalArgumentException("stick cannot be null.");
                stickToDeadzone[stick.ordinal()] = deadzone;
        }

        public void addMouseDelta (Stick stick, float mouseDeltaX, float mouseDeltaY) {
                mouseDeltaStick = stick;
                this.mouseDeltaX += mouseDeltaX;
                this.mouseDeltaY += mouseDeltaY;
        }

        public Stick getMouseDeltaStick () {
                return mouseDeltaStick;
        }

        public float[] getMouseDelta () {
                float[] mouseDelta = new float[] {mouseDeltaX, mouseDeltaY};
                mouseDeltaX = 0;
                mouseDeltaY = 0;
                return mouseDelta;
        }

        /**
         * Changes made to the device in the current thread will not actually be applied until {@link #apply()} is called.
         * @throws IOException When communication with the device fails.
         */
        public synchronized void collect () throws IOException {
                apply();
                collectingChangesThread = Thread.currentThread();
                System.arraycopy(buttonStates, 0, snapshotButtonStates, 0, buttonStates.length);
                System.arraycopy(axisStates, 0, snapshotAxisStates, 0, axisStates.length);
        }

        /**
         * Applies changes to the device made since {@link #collect()} was called.
         * @throws IOException When communication with the device fails.
         */
        public synchronized void apply () throws IOException {
                if (collectingChangesThread == null) return;
                collectingChangesThread = null;

                boolean[] targetButtonStates = buttonStates;
                buttonStates = snapshotButtonStates;
                snapshotButtonStates = targetButtonStates;

                Button[] buttons = Button.values();
                for (int i = 0, n = targetButtonStates.length; i < n; i++)
                        set(buttons[i], targetButtonStates[i]);

                float[] targetAxisStates = axisStates;
                axisStates = snapshotAxisStates;
                snapshotAxisStates = targetAxisStates;

                applyDeadzones(Stick.left, targetAxisStates[Axis.leftStickX.ordinal()], targetAxisStates[Axis.leftStickY.ordinal()]);
                applyDeadzones(Stick.right, targetAxisStates[Axis.rightStickX.ordinal()], targetAxisStates[Axis.rightStickY.ordinal()]);
                set(Axis.leftTrigger, targetAxisStates[Axis.leftTrigger.ordinal()]);
                set(Axis.rightTrigger, targetAxisStates[Axis.rightTrigger.ordinal()]);
        }

        /**
         * Sets the stick deflection after compensating for the deadzone.
         * @throws IOException When communication with the device fails.
         */
        private void applyDeadzones (Stick stick, float targetX, float targetY) throws IOException {
                float[] deflection;
                Deadzone deadzone = stickToDeadzone[stick.ordinal()];
                if (deadzone == null)
                        deflection = new float[] {targetX, targetY};
                else
                        deflection = deadzone.getOutput(targetX, targetY);

                Axis axisX = stick.getAxisX();
                int indexX = axisX.ordinal();
                float deflectionX = deflection[0];
                if (deflectionX != axisDeflections[indexX]) {
                        setAxis(axisX, deflectionX);
                        axisDeflections[indexX] = deflectionX;
                        notifyAxisChanged(axisX, targetX);
                }
                axisStates[indexX] = targetX;

                Axis axisY = stick.getAxisY();
                int indexY = axisY.ordinal();
                float deflectionY = deflection[1];
                if (deflectionY != axisDeflections[indexY]) {
                        setAxis(axisY, deflectionY);
                        axisDeflections[indexY] = deflectionY;
                        notifyAxisChanged(axisY, targetY);
                }
                axisStates[indexY] = targetY;
        }

        /**
         * Adds a listener to be notified when the device manipulates a button or axis.
         */
        public void addListener (Listener listener) {
                listeners.addListener(listener);
        }

        public void removeListener (Listener listener) {
                listeners.removeListener(listener);
        }

        /**
         * Returns the target with the specified name or alias (case insensitive).
         */
        static public Target getTarget (String name) {
                if (name == null) throw new IllegalArgumentException("name cannot be null.");
                Target target = nameToTarget.get(name.trim().toLowerCase());
                if (target == null) throw new IllegalArgumentException("Unknown target: " + name);
                return target;
        }

        /**
         * Returns all targets.
         */
        static public List<Target> getTargets () {
                return targets;
        }

        /**
         * Listener to be notified when the device manipulates a button or axis.
         */
        static public class Listener {
                public void buttonChanged (Button button, boolean pressed) {
                }

                public void axisChanged (Axis axis, float state) {
                }

                public void deviceReset () {
                }
        }
}
