package io.github.lovelybowen.tool.utils;

/**
 * @description:
 * @author: verity zhan
 * @time: 2021/3/25 17:12
 */
public class GeoIP {

    private String ipAddress;
    private String city;
    private String country;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}

