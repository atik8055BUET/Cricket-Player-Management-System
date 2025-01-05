package Source;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class UpdateList extends Thread {
    private ArrayList<Player> currList;
    private NetworkUtil networkUtil;

    public UpdateList(ArrayList<Player> currList) {
        this.currList = currList;
        try {
            Socket socket = new Socket("localhost", 12345);
            this.networkUtil = new NetworkUtil(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        while (true) {
            try {
                networkUtil.write("Request PlayerList Update");
                Object response = networkUtil.read();
                if (response instanceof ArrayList) {
                    synchronized (currList) {
                        currList.clear();
                        currList.addAll((ArrayList<Player>) response);
                    }
                }
                Thread.sleep(1000); // Sleep for 1 second
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}