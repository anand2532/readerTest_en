package com.readerTest;

import com.rfidread.Enumeration.eReadType;
import com.rfidread.Interface.IAsynchronousMessage;
import com.rfidread.Models.GPI_Model;
import com.rfidread.Models.Tag_Model;
import com.rfidread.RFIDReader;

/* * Use the reader to actively connect to the server-side sample code
 * OpenTcpServer("192.168.1.111", "9090",log)
 * 192.168.1.111 is the server-side IP, that is, the IP of the computer used for testing
 * 9090 is the TCP server port
 *
 * Do not process code logic in the callback API OutPutTags, the callback only acts as a data broker
 * It is recommended to create another instance class to receive data from readers,
 * and the program should regularly obtain and clear the instance class data.
 * */
public class readerTest implements IAsynchronousMessage {
    public static String ConnID;
    public static Boolean connectFlag = Boolean.FALSE;//Determine if any devices are connected

    public static void main(String[] args) throws InterruptedException {
        IAsynchronousMessage log = new readerTest();

        if (RFIDReader.OpenTcpServer("192.168.1.111", "9090", log)) {
            System.out.println("TCP server monitoring succeeded!");
        } else {
            System.out.println("TCP server monitoring failed!");
        }

        //Give the reader time to respond to the server, and you need to open a separate thread to listen when developing the program.
        Thread.sleep(5000);
        if (!connectFlag) { //Judge whether there is a device connected in.
            System.out.println("No device connected in");
            return;
        }

        String sn = RFIDReader._Config.GetSN(ConnID);
        System.out.println("Reader SN is: " + sn  + "\n");

        RFIDReader._Config.Stop(ConnID);
        if (RFIDReader._Tag6C.GetEPC(ConnID, 1, eReadType.Inventory) == 0) {
            System.out.println("Start reading tags OK");
        } else {
            System.out.println("Start reading tags failed");
        }
        Thread.sleep(3000);//read 3s
        RFIDReader._Config.Stop(ConnID);//Stop reading
        RFIDReader.CloseTcpServer();//Close the connection between the reader and the server
    }

    /*
    * The program in TCP server mode gets the callback of the client connection from the reader.
    * After getting the connection ID from the callback, you can control the reader through the connection ID.
    * */
    @Override
    public void PortConnecting(String ip) {

        ConnID = ip;
        if (RFIDReader.GetServerStartUp()) {
            System.out.println("The TCP server is listening!");
            System.out.println("After the reader TCP client actively connects to this computer, \n" +
                    "the connection ID obtained through the PortConnecting callback is used to control the reader, \nand the connection ID is:" + ConnID);
            connectFlag = Boolean.TRUE;
        } else {
            System.out.println("The TCP server is not listening!");
        }
         System.out.println("\n");
    }

    @Override
    public void PortClosing(String s) {
        System.out.println("this connection is disconnected " + s + "\n");
    }

    @Override
    public void OutPutTags(Tag_Model tag) {
        System.out.println("ReaderName: " + tag._ReaderName + ", SN:" + tag._ReaderSN + ", EPC: " + tag._EPC + ", Ant: " + tag._ANT_NUM + ", TID" + tag._TID);
    }

    @Override
    public void OutPutTagsOver(String s) {
        System.out.println("End of tag reading");
    }

    @Override
    public void GPIControlMsg(String s, GPI_Model gpiModel) {

    }

    @Override
    public void OutPutScanData(String s, byte[] bytes) {

    }

    public void WriteDebugMsg(String s) {
    }


    public void WriteLog(String s) {
    }

    @Override
    public void WriteDebugMsg(String s, String s1) {

    }

    @Override
    public void WriteLog(String s, String s1) {

    }
}
