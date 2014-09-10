package com.example.WiFiReader;

public class comm {
	public static final int OPEN_SOCKETING = 1;
	public static final int OPEN_SOCKET_FAIL = 2;
	public static final int OPEN_SOCKET_SUCCESS = 3;
	
	public static final int CLOSESOCKET =4;
	public static final int ACCECP = 5;
	public static final int CONNECT_KILL = 6;
	public static final int SOCKETOPENED = 7;
	
	public static String bin2hex(byte[] bs) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer("");
        //byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0xf0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
  
    }	
	public static byte[]  getPassword(String password){
		byte[] psw = new byte[6];
		char[] temp1 = new char[]{password.charAt(0),password.charAt(1)};
		char[] temp2 = new char[]{password.charAt(2),password.charAt(3)};
		char[] temp3 = new char[]{password.charAt(4),password.charAt(5)};
		char[] temp4 = new char[]{password.charAt(6),password.charAt(6)};
		char[] temp5 = new char[]{password.charAt(8),password.charAt(9)};
		char[] temp6 = new char[]{password.charAt(10),password.charAt(11)};
		psw[0] =  getpassword1(temp1);
		psw[1] =  getpassword1(temp2);
		psw[2] =  getpassword1(temp3);
		psw[3] =  getpassword1(temp4);
		psw[4] =  getpassword1(temp5);
		psw[5] =  getpassword1(temp6);
		
		return psw;
		
	}
	public static byte getpassword1(char[] temp){
		byte psw;
		
		if(temp[0]<='9'&&temp[0]>='0')
			psw =(byte) (( temp[0]-'0')&0x0F);
		else if(temp[0]<='F'&&temp[0]>='A')
			psw =(byte) (( temp[0]-'A'+10)&0x0F);
		else if(temp[0]<='f'&&temp[0]>='a')
			psw =(byte) (( temp[0]-'a'+10)&0x0F);
		else
			return 0;
		
		if(temp[1]<='9'&&temp[1]>='0')
			psw =(byte) ((psw<<4)+(byte) (( temp[1]-'0')&0x0F));
		else if(temp[1]<='F'&&temp[1]>='A')
			psw =(byte) ((psw<<4)+(byte) (( temp[1]-'A'+10)&0x0F));
		else if(temp[1]<='f'&&temp[1]>='a')
			psw =(byte) ((psw<<4)+(byte) (( temp[1]-'a'+10)&0x0F));
		
		return psw;
	}
}
