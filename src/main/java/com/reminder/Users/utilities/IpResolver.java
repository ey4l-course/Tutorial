package com.reminder.Users.utilities;


import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpResolver {
    public static String normalizeIp (String ip){
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            System.out.println("this should print inetAddress.toString(): " + inetAddress.toString());
            if (inetAddress instanceof Inet6Address){
                byte[] addr = inetAddress.getAddress();
                if (isIpv4(addr)){
                    return String.format("%d.%d.%d.%d",
                            addr[12] & 0xff,
                            addr[13] & 0xff,
                            addr[14] & 0xff,
                            addr[15] & 0xff
                    );
                }
            }
            return inetAddress.getHostAddress();
        }catch (UnknownHostException e){
            System.out.println("IP resolver failed" + e.getMessage());
            return ip;
        }catch (Exception e){
            System.out.println("IP resolver failed" + e.getMessage());
            return "No IP was recognized";
        }
    }

    private static boolean isIpv4 (byte[] addr){
        if (addr.length != 16) return false;
        for (int i = 0; i < 10; i++) {
            if (addr[i] != 0) return false;
        }
        return (addr[10] == (byte) 0xff) && (addr[11] == (byte) 0xff);
    }
}

