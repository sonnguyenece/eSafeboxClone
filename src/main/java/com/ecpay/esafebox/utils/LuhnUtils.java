package com.ecpay.esafebox.utils;

import org.apache.commons.math3.random.RandomDataGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LuhnUtils {
	public static Long randomLong7() {
	    long leftLimit = 1000000L;
	    long rightLimit = 9999999L;
	    long generatedLong = new RandomDataGenerator().nextLong(leftLimit, rightLimit);
	    return generatedLong;
	}
	
	public static int getLuhnChecksum(String cardNo) 
	{ 
	    int nDigits = cardNo.length(); 
	  
	    int nSum = 0; 
	    boolean isSecond = false; 
	    for (int i = nDigits - 1; i >= 0; i--)  
	    { 
	  
	        int d = cardNo.charAt(i) - '0'; 
	  
	        if (isSecond == true) 
	            d = d * 2; 
	  
	        // We add two digits to handle 
	        // cases that make two digits  
	        // after doubling 
	        nSum += d / 10; 
	        nSum += d % 10; 
	  
	        isSecond = !isSecond; 
	    } 
	    return (nSum % 10); 
	}
	
	public static Long generateSerial() {
		Long long7 = randomLong7();
		log.info("long7=" + long7);
		Long long9 = 890000000 + long7;
		log.info("long9=" + long9);
		int checksum = getLuhnChecksum(String.valueOf(long9));
		log.info("checksum=" + checksum);
		Long serial = long9*10 + Long.parseLong(String.valueOf(checksum));
		log.info("serial=" + serial);
		return serial;
	}
}
