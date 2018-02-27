package com.example.awesomeness.designatedride.activities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

@SuppressWarnings("WeakerAccess")
public class ClientService {

    private Socket fd;
    private DataOutputStream sender;
    private DataInputStream receiver;

    ClientService()
    {

    }

    public boolean ConnectToServer(String hostname)
    {
        return ConnectToServer(hostname, 5000);
    }
    public boolean ConnectToServer(int port)
    {
        return ConnectToServer("localhost", port);
    }

    public boolean ConnectToServer(String hostname, int port)
    {
        if (!AuthenticateAddressnPort(hostname, port))
            return false;
        try {
            fd = new Socket(hostname, port);
            sender = new DataOutputStream(fd.getOutputStream());
            receiver = new DataInputStream(fd.getInputStream());

            return true;
        } catch (UnknownHostException e) {
            System.err.println("Error, cannot resolve hostname: " + hostname);
            return false;
        } catch (IOException e) {
            System.err.println("Error, I/O exception " + hostname);
            return false;
        }

    }

    public boolean Send(byte data[], int length)
    {
        try {
            sender.write(data, 0, length);
            return true;
        } catch (IOException e) {
            System.err.println("Error, failed to send message to server");
            return false;
        }
    }

    public int Recv(byte data[], int offset, int length, boolean flag)
    {
        int r;
        try {
            r = receiver.read(data, offset, length);
            if (flag) {
                SortBytesToLittleEndian(data, length);
            }
            return r;
        } catch (IOException e) {
            System.err.println("Error, failed to receive message from server");
            return -1;
        }
    }

    public void Close()
    {
        try {
            fd.close();
        } catch (IOException e) {
            System.err.println("Error, failed to close socket");
        }
    }

    private void SortBytesToLittleEndian(byte data[], int length)
    {
        byte tmp[] = new byte[length];
        for (int i=0; i<length; i++) {
            tmp[i] = data[length - 1 - i];
        }

        for (int i=0; i<length; i++) {
            data[i] = tmp[i];
        }

    }


    private boolean AuthenticateAddressnPort(String address, int port)
    {
        if (address == null || address.equals(""))
            return false;
        if (port <=0 || port >  65535)
            return false;

        return true;
    }
}
