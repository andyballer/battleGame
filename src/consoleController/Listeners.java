import java.lang.reflect.Array;

/**
 * Keeps a list of event listeners, optimized for iteration. Adding and removing listeners is more expensive.
 */
public class Listeners<T> {
        private final Class<T> listenerClass;
        private T[] listeners;

        public Listeners (Class<T> listenerClass) {
                this.listenerClass = listenerClass;
                listeners = (T[])Array.newInstance(listenerClass, 0);
        }

        /**
         * If the listener already exists, it is not added again.
         */
        public synchronized void addListener (T listener) {
                if (listener == null) throw new IllegalArgumentException("listener cannot be null.");
                synchronized (this) {
                        T[] listeners = this.listeners;
                        int n = listeners.length;
                        for (int i = 0; i < n; i++)
                                if (listener == listeners[i]) return;
                        T[] newListeners = (T[])Array.newInstance(listenerClass, n + 1);
                        newListeners[0] = listener;
                        System.arraycopy(listeners, 0, newListeners, 1, n);
                        this.listeners = newListeners;
                }
        }

        public synchronized void removeListener (T listener) {
                if (listener == null) throw new IllegalArgumentException("listener cannot be null.");
                T[] listeners = this.listeners;
                int n = listeners.length;
                if (n == 0) return;
                T[] newListeners = (T[])Array.newInstance(listenerClass, n - 1);
                for (int i = 0, ii = 0; i < n; i++) {
                        T copyListener = listeners[i];
                        if (listener == copyListener) continue;
                        if (ii == n - 1) return;
                        newListeners[ii++] = copyListener;
                }
                this.listeners = newListeners;
        }

        public T[] toArray () {
                return listeners;
        }
}
