package form

import javax.swing.JFrame
import javax.swing.WindowConstants


class MainForm extends JFrame implements Observer {

    @Override
    void update(Observable o, Object arg) {

    }

    int width
    int height

    MainForm(String title){
        super(title);
      //  add(panel1);
    }
}
