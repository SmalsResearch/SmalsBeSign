package be.smals.research.bulksign.desktopapp.signverify;

import java.io.FileInputStream;
import java.io.IOException;


/*
 * Copyright (c) Smals
 */

/**
 * Tests the ComputeMessageDigest class
 * Takes 3 files from the hard drive
 * Returns the corresponding MasterDigest
 */
public class TestComputeMasterDigest {

    public static void main(String[] args) {

        try {
            FileInputStream[] input = new FileInputStream[3];

            input[0] = new FileInputStream("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\Test1.txt");
            input[1] = new FileInputStream("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\Test2.txt");
            input[2] = new FileInputStream("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\Test3.txt");

            String MasterDigest;

            MasterDigest = ComputeMasterDigest.main(input);
            System.out.println(MasterDigest);

            input[0].close();
            input[1].close();
            input[2].close();

        } catch (IOException e) {
            System.out.print("Exception");

        }

    }


}
