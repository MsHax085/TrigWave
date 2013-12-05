
package trigwave;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 *
 * @author Richard
 * @since 2013-dec-05
 * @version 1.0
 */
public class FrameListener implements WindowListener {
    
    private final TrigWave trigWave;
    
    public FrameListener(final TrigWave trigWave) {
        this.trigWave = trigWave;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        trigWave.close();
    }
    
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
