package es.pnia.espmote;

import java.net.InetAddress;

public class Item {

    private int image;
    private String name;
    private InetAddress ip;

    public Item() {
        super();
    }

    public Item(int image, String name, InetAddress ip) {
        super();
        this.image = image;
        this.name = name;
        this.ip = ip;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

}