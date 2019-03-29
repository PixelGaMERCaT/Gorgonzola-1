// This example illustrates how C++ classes can be used from Java using SWIG.
// The Java class gets mapped onto the C++ class and behaves as if it is a Java class.

//import com.kauailabs.vmx.*;

public class VMXInterface {
	/*static {
		try {
			System.loadLibrary("vmxpi_hal_java");
		} catch (UnsatisfiedLinkError e) {
			System.err.println(
					"Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help.\n"
							+ e);
			System.exit(1);
		}
	}

	static void DisplayVMXError(int vmxerr) {
		String err_description = vmxpi_hal_java.GetVMXErrorString(vmxerr);
		System.out.println("VMXError " + vmxerr + ":  " + err_description);
	}
	DirectBuffer rx_bytes;	
	DirectBuffer tx_bytes;
	VMXPi vmx;
	int[] vmxerr;
	short[] spi_res_handle;
	public void init(String argv[]) {
		vmx = new VMXPi(false, (byte) 50);
		if (vmx.IsOpen()) {

			vmxerr = new int[]{ 0 };
			spi_res_handle = new short[]{ 0 };
			VMXChannelInfo[] spi_channels = {
					new VMXChannelInfo(vmx.getIO().GetSoleChannelIndex(VMXChannelCapability.SPI_CLK),
							VMXChannelCapability.SPI_CLK),
					new VMXChannelInfo(vmx.getIO().GetSoleChannelIndex(VMXChannelCapability.SPI_MOSI),
							VMXChannelCapability.SPI_MOSI),
					new VMXChannelInfo(vmx.getIO().GetSoleChannelIndex(VMXChannelCapability.SPI_MISO),
							VMXChannelCapability.SPI_MISO),
					new VMXChannelInfo(vmx.getIO().GetSoleChannelIndex(VMXChannelCapability.SPI_CS),
							VMXChannelCapability.SPI_CS) };

			SPIConfig spi_cfg = new SPIConfig(1000000 /* Bitrate);
			if (!vmx.getIO().ActivateQuadchannelResource(spi_channels[0], spi_channels[1], spi_channels[2],
					spi_channels[3], spi_cfg, spi_res_handle, vmxerr)) {
				System.out.println("Failed to Activate SPI Resource.\n");
				DisplayVMXError(vmxerr[0]);
				vmx.getIO().DeallocateResource(spi_res_handle[0], vmxerr);
			} else {
//			System.out.printf("Successfully Activated SPI Resource with VMXChannels %d, %d, %d and %d\n", 
//				spi_channels[0].getIndex(), 
//				spi_channels[1].getIndex(), 
//				spi_channels[2].getIndex(), 
//				spi_channels[3].getIndex());
			}

			/* Allocate native (direct) buffers 
			rx_bytes = new DirectBuffer(10);	
			tx_bytes = new DirectBuffer(10);
			
		} else {
			System.out.println("Error:  Unable to open VMX Client.");
			System.out.println("");
			System.out.println("        - Is pigpio (or the system resources it requires) in use by another process?");
			System.out.println("        - Does this application have root privileges?");
		}

		//vmx.delete(); // Immediately dispose of all resources used by vmx object.
	}
	public double getYaw() {

		tx_bytes.setitem(0, (byte) 1);
		tx_bytes.setitem(1, (byte) 2);
		tx_bytes.setitem(2, (byte) 3);
		tx_bytes.setitem(3, (byte) 4);
		tx_bytes.setitem(4, (byte) 5);
		if (!vmx.getIO().SPI_Transaction(spi_res_handle[0], tx_bytes.cast(), rx_bytes.cast(), 10, vmxerr)) {
			System.out.println("SPI Transaction Failed.\n");
			DisplayVMXError(vmxerr[0]);
			return 0;
		} else {
			//System.out.print("SPI Transaction complete.  rx_data:  ");
			//for (int j = 5; j < 10; j++) {
			//	System.out.print((byte) rx_bytes.getitem(j));
			//}
			System.out.println(vmx.getAHRS().GetYaw());
			return (vmx.getAHRS().GetYaw());
		}
		

		vmx.getTime().DelayMilliseconds(20);

	}*/
}