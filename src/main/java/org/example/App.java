package org.example;

import java.awt.*;

public class App 
{
    public static void main( String[] args ) throws AWTException {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();

            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image);
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);

            trayIcon.displayMessage("Reception d'un nouveau Mail", "Vous venez de recevoir un nouveau mail!", TrayIcon.MessageType.INFO);
        } else {
            System.err.println("System tray not supported!");
        }
        System.exit(1);
    }
}
