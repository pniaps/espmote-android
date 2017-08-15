package es.pnia.espmote;

import java.net.InetAddress;

public class Item {

    private int image;
    private String name;
    private InetAddress ip;
    private Boolean channel0;
    private Boolean channel1;
    private Boolean channel2;

    public Item() {
        super();
    }

    public Boolean isChannel0() {
        return channel0;
    }

    public Boolean isChannel1() {
        return channel1;
    }

    public Boolean isChannel2() {
        return channel2;
    }

    public void setChannel0(Boolean channel0) {

        this.channel0 = channel0;
    }

    public void setChannel1(Boolean channel1) {
        this.channel1 = channel1;
    }

    public void setChannel2(Boolean channel2) {
        this.channel2 = channel2;
    }

    public Item(int image, String name, InetAddress ip) {
        super();
        this.image = image;
        this.name = name;
        this.ip = ip;
        this.channel0 = false;
        this.channel1 = false;
        this.channel2 = false;
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