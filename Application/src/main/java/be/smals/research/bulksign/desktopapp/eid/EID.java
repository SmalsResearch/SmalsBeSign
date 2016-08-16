/*
 * eID Applet Project.
 * Copyright (C) 2008-2012 FedICT.
 * Copyright (C) 2014-2015 e-Contract.be BVBA.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see 
 * http://www.gnu.org/licenses/.
 */

package be.smals.research.bulksign.desktopapp.eid;

import be.smals.research.bulksign.desktopapp.eid.external.UserCancelledException;
import be.smals.research.bulksign.desktopapp.eid.external.sc.Constants;

import javax.imageio.ImageIO;
import javax.smartcardio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.List;

/**
 * Holds the functions related to eID card access over PC/SC.
 */
public class EID {

	public static final int MIN_PIN_SIZE = 4;
	public static final int MAX_PIN_SIZE = 12;
	public static final int PUK_SIZE = 6;
	public static final byte AUTHN_KEY_ID = (byte) 0x82;
	public static final byte NON_REP_KEY_ID = (byte) 0x83;

	private final static byte[] ATR_PATTERN = new byte[] { 0x3b, (byte) 0x98, 0x00, 0x40, 0x00, (byte) 0x00, 0x00, 0x00,
			0x01, 0x01, (byte) 0xad, 0x13, 0x10 };
	private final static byte[] ATR_MASK = new byte[] { (byte) 0xff, (byte) 0xff, 0x00, (byte) 0xff, 0x00, 0x00, 0x00,
			0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xf0 };

	public static final byte[] IDENTITY_FILE_ID = new byte[] { 0x3F, 0x00, (byte) 0xDF, 0x01, 0x40, 0x31 };
	public static final byte[] IDENTITY_SIGN_FILE_ID = new byte[] { 0x3F, 0x00, (byte) 0xDF, 0x01, 0x40, 0x32 };
	public static final byte[] ADDRESS_FILE_ID = new byte[] { 0x3F, 0x00, (byte) 0xDF, 0x01, 0x40, 0x33 };
	public static final byte[] ADDRESS_SIGN_FILE_ID = new byte[] { 0x3F, 0x00, (byte) 0xDF, 0x01, 0x40, 0x34 };
	public static final byte[] PHOTO_FILE_ID = new byte[] { 0x3F, 0x00, (byte) 0xDF, 0x01, 0x40, 0x35 };
	public static final byte[] AUTHN_CERT_FILE_ID = new byte[] { 0x3F, 0x00, (byte) 0xDF, 0x00, 0x50, 0x38 };
	public static final byte[] SIGN_CERT_FILE_ID = new byte[] { 0x3F, 0x00, (byte) 0xDF, 0x00, 0x50, 0x39 };
	public static final byte[] CA_CERT_FILE_ID = new byte[] { 0x3F, 0x00, (byte) 0xDF, 0x00, 0x50, 0x3A };
	public static final byte[] ROOT_CERT_FILE_ID = new byte[] { 0x3F, 0x00, (byte) 0xDF, 0x00, 0x50, 0x3B };
	public static final byte[] RRN_CERT_FILE_ID = new byte[] { 0x3F, 0x00, (byte) 0xDF, 0x00, 0x50, 0x3C };
	public static final byte[] BELPIC_AID = new byte[] { (byte) 0xA0, 0x00, 0x00, 0x01, 0x77, 0x50, 0x4B, 0x43, 0x53,
			0x2D, 0x31, 0x35 };
	public static final byte[] APPLET_AID = new byte[] { (byte) 0xA0, 0x00, 0x00, 0x00, 0x30, 0x29, 0x05, 0x70, 0x00,
			(byte) 0xAD, 0x13, 0x10, 0x01, 0x01, (byte) 0xFF };

	public static final byte FEATURE_VERIFY_PIN_START_TAG = 0x01;
	public static final byte FEATURE_VERIFY_PIN_FINISH_TAG = 0x02;
	public static final byte FEATURE_MODIFY_PIN_START_TAG = 0x03;
	public static final byte FEATURE_MODIFY_PIN_FINISH_TAG = 0x04;
	public static final byte FEATURE_GET_KEY_PRESSED_TAG = 0x05;
	public static final byte FEATURE_VERIFY_PIN_DIRECT_TAG = 0x06;
	public static final byte FEATURE_MODIFY_PIN_DIRECT_TAG = 0x07;
	public static final byte FEATURE_EID_PIN_PAD_READER_TAG = (byte) 0x80;

	private static final int BLOCK_SIZE = 0xff;

	private final TerminalFactory terminalFactory;
	private List<CardTerminal> cardTerminalList;
	private CardChannel cardChannel;
	private CardTerminal cardTerminal;
	private Card card;
	private Set<String> ppduNames = new HashSet<>();

	private static class ListData {
		private CardTerminal cardTerminal;
		private BufferedImage photo;

		public ListData(CardTerminal cardTerminal, BufferedImage photo) {
			this.cardTerminal = cardTerminal;
			this.photo = photo;
		}

		public CardTerminal getCardTerminal() {
			return this.cardTerminal;
		}

		public BufferedImage getPhoto() {
			return this.photo;
		}
	}
	private static class EidListCellRenderer extends JPanel implements ListCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
													  boolean cellHasFocus) {
			JPanel panel = new JPanel();
			ListData listData = (ListData) value;
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel photoLabel = new JLabel(new ImageIcon(listData.getPhoto()));
			panel.add(photoLabel);
			JLabel nameLabel = new JLabel(listData.getCardTerminal().getName());
			if (isSelected) {
				panel.setBackground(list.getSelectionBackground());
			} else {
				panel.setBackground(list.getBackground());
			}
			panel.add(nameLabel);
			return panel;
		}
	}
	private static class CCIDFeature {
		private final byte feature;
		private final Integer ioctl;

		public CCIDFeature(byte feature) {
			this.feature = feature;
			this.ioctl = null; // PPDU
		}

		public CCIDFeature(byte feature, Integer ioctl) {
			this.feature = feature;
			this.ioctl = ioctl;
		}

		public Integer getIoctl() {
			return this.ioctl;
		}

		public ResponseAPDU transmit(byte[] command, Card card, CardChannel cardChannel) throws CardException {
			if (this.ioctl == null) {
				// PPDU
				return cardChannel.transmit(new CommandAPDU(0xff, 0xc2, 0x01, this.feature, command));
			} else {
				byte[] result = card.transmitControlCommand(this.ioctl, command);
				ResponseAPDU responseApdu = new ResponseAPDU(result);
				return responseApdu;
			}
		}

		public byte[] transmitByteResponse(byte[] command, Card card, CardChannel cardChannel) throws CardException {
			if (this.ioctl == null) {
				// PPDU
				return cardChannel.transmit(new CommandAPDU(0xff, 0xc2, 0x01, this.feature, command)).getData();
			} else {
				byte[] result = card.transmitControlCommand(this.ioctl, command);
				return result;
			}
		}
	}

	/**
	 * Constructor - Initializes the terminal factory
     */
	public EID() {
		this.terminalFactory = TerminalFactory.getDefault();
	}

	// ----- Basic operations ------------------------------------------------------------------------------------------
	public byte[] readFile(byte[] fileId) throws CardException, IOException {
		selectFile(fileId);
		byte[] data = readBinary();
		return data;
	}
	public void close() throws CardException {
		// this.card.endExclusive();
		this.card.disconnect(true);
	}
	private byte[] readBinary() throws CardException, IOException {
		int offset = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] data;
		do {
			CommandAPDU readBinaryApdu = new CommandAPDU(0x00, 0xB0, offset >> 8, offset & 0xFF, BLOCK_SIZE);
			ResponseAPDU responseApdu = transmit(readBinaryApdu);
			int sw = responseApdu.getSW();
			if (0x6B00 == sw) {
				/*
				 * Wrong parameters (offset outside the EF) End of file reached.
				 * Can happen in case the file size is a multiple of 0xff bytes.
				 */
				break;
			}
			if (0x9000 != sw) {
				throw new IOException("APDU response error: " + responseApdu.getSW());
			}

			/*
			 * Introduce some delay for old Belpic V1 eID cards.
			 */
			// try {
			// Thread.sleep(50);
			// } catch (InterruptedException e) {
			// throw new RuntimeException("sleep error: " + e.getMessage(), e);
			// }
			data = responseApdu.getData();
			baos.write(data);
			offset += data.length;
		} while (BLOCK_SIZE == data.length);
		return baos.toByteArray();
	}
	private ResponseAPDU transmit(CommandAPDU commandApdu) throws CardException {
		ResponseAPDU responseApdu = this.cardChannel.transmit(commandApdu);
		if (0x6c == responseApdu.getSW1()) {
			/*
			 * A minimum delay of 10 msec between the answer ‘6C xx’ and the
			 * next APDU is mandatory for eID v1.0 and v1.1 cards.
			 */
			System.out.println("sleeping...");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException("cannot sleep");
			}
			responseApdu = this.cardChannel.transmit(commandApdu);
		}
		return responseApdu;
	}

	// ----- Getters & Setters -----------------------------------------------------------------------------------------
	public Card getCard() {
		return this.card;
	}
	public CardChannel getCardChannel() {
		return this.cardChannel;
	}
	public boolean hasCardReader() throws CardException {
		TerminalFactory factory = TerminalFactory.getDefault();
		CardTerminals cardTerminals = factory.terminals();
		List<CardTerminal> terminalList = cardTerminals.list();
		if (!terminalList.isEmpty()) {
			cardTerminalList = terminalList;
			return true;
		}
		return false;
	}
	public boolean isEidPresent() throws CardException {
		if (null == cardTerminalList || cardTerminalList.isEmpty()) {

			try {
				cardTerminalList = terminalFactory.terminals().list();
			} catch (CardException e) {
				// Failed to get reader(s)
				Throwable cause = e.getCause();
				if (null != cause) {
					/*
					 * Windows can give us a
					 * sun.security.smartcardio.PCSCException
					 * SCARD_E_NO_READERS_AVAILABLE when no card readers are
					 * connected to the system.
					 */
					if ("SCARD_E_NO_READERS_AVAILABLE".equals(cause.getMessage())) {
						// Windows only - NO reader available
					}
				}
				e.printStackTrace();
				return false;
			}

		}

		Set<CardTerminal> eIDCardTerminals = new HashSet<CardTerminal>();
		for (CardTerminal cardTerminal : cardTerminalList) {
			// on OS X isCardPresent or waitForxxx calls on the card terminal
			// don't work
			// you need to connect to it to see if there is a card present...
			boolean cardPresent;
			try {
				cardPresent = cardTerminal.isCardPresent();
			} catch (CardException e) {
				cardPresent = false;
			}
			if (cardPresent || this.isOSX()) {
				Card card;
				try {
					/*
					 * eToken is not using T=0 apparently, hence the need for an
					 * explicit CardException catch
					 */
					card = cardTerminal.connect("T=0");
					/*
					 * The exclusive card lock in combination with reset at
					 * disconnect and some sleeps seems to fix the
					 * SCARD_E_SHARING_VIOLATION issue.
					 */
					card.beginExclusive();
				} catch (CardException e) {
					// Could not connect to card
					continue;
				}
				ATR atr = card.getATR();
				if (matchesEidAtr(atr)) {
					eIDCardTerminals.add(cardTerminal);
				} else {
					byte[] atrBytes = atr.getBytes();
					StringBuffer atrStringBuffer = new StringBuffer();
					for (byte atrByte : atrBytes) {
						atrStringBuffer.append(Integer.toHexString(atrByte & 0xff));
					}
					// Not a supported eID card. ATR= " + atrStringBuffer;
				}
				card.endExclusive(); // SCARD_E_SHARING_VIOLATION fix
				card.disconnect(true);
			}
		}
		if (eIDCardTerminals.isEmpty()) {
			return false;
		}
		if (eIDCardTerminals.size() == 1) {
			this.cardTerminal = eIDCardTerminals.iterator().next();
		} else {
			try {
				this.cardTerminal = selectCardTerminal(eIDCardTerminals);
			} catch (IOException e) {
				return false;
			}
		}
		if (null == this.cardTerminal) {
			/*
			 * In case the card terminal selection was canceled.
			 */
			return false;
		}
		// eID card detected in card terminal : " + this.cardTerminal.getName());
		this.card = this.cardTerminal.connect("T=0");
		this.card.beginExclusive();
		this.cardChannel = card.getBasicChannel();
		this.card.endExclusive();
		return true;
	}
	public boolean isCardStillPresent() throws CardException {
		return this.cardTerminal.isCardPresent();
	}
	private boolean isPPDUCardTerminal(String name) {
		name = name.toLowerCase();
		for (String ppduName : this.ppduNames) {
			if (name.contains(ppduName)) {
				return true;
			}
		}
		return false;
	}
	private Map<Byte, CCIDFeature> getCCIDFeatures() {
		final boolean onMsWindows = (System.getProperty("os.name") != null
				&& System.getProperty("os.name").startsWith("Windows"));
//		this.view.addDetailMessage("CCID GET_FEATURE IOCTL...");
		int ioctl;
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			ioctl = (0x31 << 16 | (3400) << 2);
		} else {
			ioctl = 0x42000D48;
		}
		byte[] features;
		try {
			features = this.card.transmitControlCommand(ioctl, new byte[0]);
			Map<Byte, CCIDFeature> ccidFeatures = new HashMap<Byte, EID.CCIDFeature>();
			int idx = 0;
			while (idx < features.length) {
				byte tag = features[idx];
				idx++;
				idx++;
				int featureIoctl = 0;
				for (int count = 0; count < 3; count++) {
					featureIoctl |= features[idx] & 0xff;
					idx++;
					featureIoctl <<= 8;
				}
				featureIoctl |= features[idx] & 0xff;
				idx++;
				ccidFeatures.put(tag, new CCIDFeature(tag, featureIoctl));
			}
			if (ccidFeatures.isEmpty() && onMsWindows && isPPDUCardTerminal(this.cardTerminal.getName())) {
				// Windows 10 work-around
//				this.view.addDetailMessage("trying PPDU interface...");
				ResponseAPDU responseAPDU = this.cardChannel
						.transmit(new CommandAPDU((byte) 0xff, (byte) 0xc2, 0x01, 0x00, new byte[] {}, 32));
//				this.view.addDetailMessage("PPDU response: " + Integer.toHexString(responseAPDU.getSW()));
				if (responseAPDU.getSW() == 0x9000) {
					features = responseAPDU.getData();
					for (byte feature : features) {
						ccidFeatures.put(feature, new CCIDFeature(feature));
//						this.view.addDetailMessage("PPDU feature: " + feature);
					}
					return ccidFeatures;
				} else {
					return Collections.EMPTY_MAP;
				}
			}
			return ccidFeatures;
		} catch (CardException e) {
//			this.view.addDetailMessage("GET_FEATURES IOCTL error: " + e.getMessage());
			try {
				if (!onMsWindows || !isPPDUCardTerminal(this.cardTerminal.getName())) {
					return Collections.EMPTY_MAP;
				}
				// try pseudo-APDU (PPDU) interface
//				this.view.addDetailMessage("trying PPDU interface...");
				ResponseAPDU responseAPDU = this.cardChannel
						.transmit(new CommandAPDU((byte) 0xff, (byte) 0xc2, 0x01, 0x00, new byte[] {}, 32));
//				this.view.addDetailMessage("PPDU response: " + Integer.toHexString(responseAPDU.getSW()));
				if (responseAPDU.getSW() == 0x9000) {
					Map<Byte, CCIDFeature> ccidFeatures = new HashMap<Byte, EID.CCIDFeature>();
					features = responseAPDU.getData();
					for (byte feature : features) {
						ccidFeatures.put(feature, new CCIDFeature(feature));
//						this.view.addDetailMessage("PPDU feature: " + feature);
					}
					return ccidFeatures;
				} else {
					return Collections.EMPTY_MAP;
				}
			} catch (CardException e2) {
//				this.view.addDetailMessage("PPDU failed: " + e2.getMessage());
				Throwable cause = e2.getCause();
				if (null != cause) {
//					this.view.addDetailMessage("cause: " + cause.getMessage());
					StackTraceElement[] stackTrace = cause.getStackTrace();
					for (StackTraceElement stackTraceElement : stackTrace) {
//						this.view.addDetailMessage("at " + stackTraceElement.getClassName() + "."
//								+ stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber());
					}
				}
//				selectBelpicJavaCardApplet();
				return Collections.EMPTY_MAP;
			}
		} finally {
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				// woops
			}
		}
	}
	public List<String> getReaderList() throws CardException {
		List<String> readerList = new LinkedList<String>();
		TerminalFactory factory = TerminalFactory.getDefault();
		CardTerminals cardTerminals = factory.terminals();

		List<CardTerminal> cardTerminalList = cardTerminals.list();

		for (CardTerminal cardTerminal : cardTerminalList) {
			readerList.add(cardTerminal.getName());
		}
		return readerList;
	}
	public List<X509Certificate> getSignCertificateChain() throws CardException, IOException, CertificateException {
		List<X509Certificate> signCertificateChain = new LinkedList<X509Certificate>();
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

		byte[] signCertFile = readFile(SIGN_CERT_FILE_ID);
		X509Certificate signCert = (X509Certificate) certificateFactory
				.generateCertificate(new ByteArrayInputStream(signCertFile));

		byte[] citizenCaCertFile = readFile(CA_CERT_FILE_ID);
		X509Certificate citizenCaCert = (X509Certificate) certificateFactory
				.generateCertificate(new ByteArrayInputStream(citizenCaCertFile));

//		this.view.addDetailMessage("reading Root CA certificate...");
		byte[] rootCaCertFile = readFile(ROOT_CERT_FILE_ID);
		X509Certificate rootCaCert = (X509Certificate) certificateFactory
				.generateCertificate(new ByteArrayInputStream(rootCaCertFile));

		signCertificateChain.add(rootCaCert);
		signCertificateChain.add(citizenCaCert);
		signCertificateChain.add(signCert);

		return signCertificateChain;
	}

	// ----- Wait for --------------------------------------------------------------------------------------------------
	public void waitForEidPresent() throws CardException, InterruptedException {
		while (true) {

			if (isOSX()) {

				// on OS X, waitForChange does not work to detect a card, only
				// way is to try to connect to it and see what happens
//				this.view.addDetailMessage("sleeping...");
				Thread.sleep(1000);
				if (isEidPresent()) {
					return;
				}

			} else {

				try {
					terminalFactory.terminals().waitForChange();
				} catch (CardException e) {
//					this.view.addDetailMessage("card error: " + e.getMessage());
					Throwable cause = e.getCause();
					if (null != cause) {
						if ("SCARD_E_NO_READERS_AVAILABLE".equals(cause.getMessage())) {
							/*
							 * sun.security.smartcardio.PCSCException
							 *
							 * Windows platform.
							 */
//							this.view.addDetailMessage("no readers available.");
//							this.view.setStatusMessage(Status.NORMAL, MESSAGE_ID.CONNECT_READER);
						}
					}
//					this.view.addDetailMessage("sleeping...");
					Thread.sleep(1000);
				} catch (IllegalStateException e) {
//					this.view.addDetailMessage("no terminals at all. sleeping...");
//					this.view.addDetailMessage("Maybe you should connect a smart card reader?");
					if (System.getProperty("os.name").startsWith("Linux")) {
//						this.view.addDetailMessage("Maybe the pcscd service is not running?");
					}
					Thread.sleep(1000);
				}
				Thread.sleep(50); // SCARD_E_SHARING_VIOLATION fix
				if (isEidPresent()) {
					return;
				}
			}
		}
	}
	public void waitForCardReader() {
		try {
			TerminalFactory terminalFactory = TerminalFactory.getDefault();
			CardTerminals terminals = terminalFactory.terminals();

			List<CardTerminal> terminalList;
			try {
				terminalList = terminals.list();
			} catch (CardException e) {
				terminalList = Collections.emptyList();
			}
			while (terminalList.isEmpty()) {
				Thread.sleep(2000);
				terminals = terminalFactory.terminals();
				try {
					terminalList = terminals.list();
				} catch (CardException e) {
					terminalList = Collections.emptyList();
				}
			}

			this.cardTerminalList = terminalList;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private CardTerminal selectCardTerminal(Set<CardTerminal> eIDCardTerminals) throws CardException, IOException {
		// Multiple eID card detected...
		DefaultListModel listModel = new DefaultListModel();
		for (CardTerminal cardTerminal : eIDCardTerminals) {
			this.cardTerminal = cardTerminal;
			this.card = this.cardTerminal.connect("T=0");
			this.card.beginExclusive();
			this.cardChannel = this.card.getBasicChannel();

			// Reading photo from: " + this.cardTerminal.getName());
			byte[] photoFile = readFile(PHOTO_FILE_ID);
			BufferedImage photo = ImageIO.read(new ByteArrayInputStream(photoFile));
			listModel.addElement(new ListData(cardTerminal, photo));

			this.card.endExclusive(); // SCARD_E_SHARING_VIOLATION fix
			this.card.disconnect(true);
		}

		final JDialog dialog = new JDialog((Frame) null, "Select eID card", true);
		final ListData selectedListData = new ListData(null, null);
		dialog.setLayout(new BorderLayout());

		JList list = new JList(listModel);
		list.setCellRenderer(new EidListCellRenderer());
		dialog.getContentPane().add(list);

		MouseListener mouseListener = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				JList theList = (JList) mouseEvent.getSource();
				if (mouseEvent.getClickCount() == 2) {
					int index = theList.locationToIndex(mouseEvent.getPoint());
					if (index >= 0) {
						Object object = theList.getModel().getElementAt(index);
						ListData listData = (ListData) object;
						selectedListData.cardTerminal = listData.cardTerminal;
						selectedListData.photo = listData.photo;
						dialog.dispose();
					}
				}
			}
		};
		list.addMouseListener(mouseListener);

		dialog.pack();
//		dialog.setLocationRelativeTo(this.view.getParentComponent());
		dialog.setResizable(false);
		dialog.setAlwaysOnTop(true);

		dialog.setVisible(true);

		return selectedListData.getCardTerminal();
	}
	private void selectFile(byte[] fileId) throws CardException, FileNotFoundException {
//		this.view.addDetailMessage("selecting file");
		CommandAPDU selectFileApdu = new CommandAPDU(0x00, 0xA4, 0x08, 0x0C, fileId);
		ResponseAPDU responseApdu = transmit(selectFileApdu);
		if (0x9000 != responseApdu.getSW()) {
			throw new FileNotFoundException(
					"wrong status word after selecting file: " + Integer.toHexString(responseApdu.getSW()));
		}
		try {
			// SCARD_E_SHARING_VIOLATION fix
			Thread.sleep(20);
		} catch (InterruptedException e) {
			throw new RuntimeException("sleep error: " + e.getMessage());
		}
	}
	private boolean matchesEidAtr(ATR atr) {
		byte[] atrBytes = atr.getBytes();
		if (atrBytes.length != ATR_PATTERN.length) {
			return false;
		}
		for (int idx = 0; idx < atrBytes.length; idx++) {
			atrBytes[idx] &= ATR_MASK[idx];
		}
		if (Arrays.equals(atrBytes, ATR_PATTERN)) {
			return true;
		}
		return false;
	}
	public void addPPDUName(String name) {
		this.ppduNames.add(name.toLowerCase());
	}

	// ----- Sign ------------------------------------------------------------------------------------------------------
	public void prepareSigning (String digestAlgo, byte keyId) throws CardException {
		// select the key
		byte algoRef;
		if ("SHA-1-PSS".equals(digestAlgo)) {
			algoRef = 0x10;
		} else if ("SHA-256-PSS".equals(digestAlgo)) {
			algoRef = 0x20;
		} else {
			algoRef = 0x01; // PKCS#1
		}
		CommandAPDU setApdu = new CommandAPDU(0x00, 0x22, 0x41, 0xB6,
				new byte[] { 0x04, // length of following data
						(byte) 0x80, // algo ref
						algoRef, (byte) 0x84, // tag for private key ref
						keyId });
		ResponseAPDU responseApdu = transmit(setApdu);
		if (0x9000 != responseApdu.getSW()) {
			throw new RuntimeException("SELECT error");
		}
	}
	public byte[] signAlt (byte[] digest, String digestAlgo) throws IOException, CardException {
		ByteArrayOutputStream digestInfo = new ByteArrayOutputStream();
		if ("SHA-1".equals(digestAlgo) || "SHA1".equals(digestAlgo)) {
			digestInfo.write(Constants.SHA1_DIGEST_INFO_PREFIX);
		} else if ("SHA-224".equals(digestAlgo)) {
			digestInfo.write(Constants.SHA224_DIGEST_INFO_PREFIX);
		} else if ("SHA-256".equals(digestAlgo)) {
			digestInfo.write(Constants.SHA256_DIGEST_INFO_PREFIX);
		} else if ("SHA-384".equals(digestAlgo)) {
			digestInfo.write(Constants.SHA384_DIGEST_INFO_PREFIX);
		} else if ("SHA-512".equals(digestAlgo)) {
			digestInfo.write(Constants.SHA512_DIGEST_INFO_PREFIX);
		} else if ("RIPEMD160".equals(digestAlgo)) {
			digestInfo.write(Constants.RIPEMD160_DIGEST_INFO_PREFIX);
		} else if ("RIPEMD128".equals(digestAlgo)) {
			digestInfo.write(Constants.RIPEMD128_DIGEST_INFO_PREFIX);
		} else if ("RIPEMD256".equals(digestAlgo)) {
			digestInfo.write(Constants.RIPEMD256_DIGEST_INFO_PREFIX);
		} else if (Constants.PLAIN_TEXT_DIGEST_ALGO_OID.equals(digestAlgo)) {
			byte[] digestInfoPrefix = Arrays.copyOf(Constants.PLAIN_TEXT_DIGEST_INFO_PREFIX,
					Constants.PLAIN_TEXT_DIGEST_INFO_PREFIX.length);
			digestInfoPrefix[1] = (byte) (digest.length + 13);
			digestInfoPrefix[14] = (byte) digest.length;
			digestInfo.write(digestInfoPrefix);
		} else if ("SHA-1-PSS".equals(digestAlgo)) {
			// no prefix required
		} else if ("SHA-256-PSS".equals(digestAlgo)) {
			// no prefix required
		} else {
			throw new RuntimeException("Digest Algorithm not supported: " + digestAlgo);
		}
		digestInfo.write(digest);
		CommandAPDU computeDigitalSignatureApdu = new CommandAPDU(0x00, 0x2A, 0x9E, 0x9A, digestInfo.toByteArray());
		System.out.println("COMMAND: "+Arrays.toString(computeDigitalSignatureApdu.getBytes()));

//		this.view.addDetailMessage("computing digital signature...");
		ResponseAPDU responseApdu = transmit(computeDigitalSignatureApdu);
		System.out.println("DATA:");
		System.out.println(Arrays.toString(responseApdu.getData()));
		System.out.println("BYTES:");
		System.out.println(Arrays.toString(responseApdu.getBytes()));
		System.out.println("OTHER: "+" "+responseApdu.toString());
		if (0x9000 == responseApdu.getSW()) {
			/*
			 * OK, we could use the card PIN caching feature.
			 *
			 * Notice that the card PIN caching also works when first doing an
			 * authentication after a non-repudiation signature.
			 */
			byte[] signatureValue = responseApdu.getData();
			return signatureValue;
		}
		return null;
	}

	// ----- PIN validity check ----------------------------------------------------------------------------------------
	public boolean isPinValid (char[] pin) throws CardException, UserCancelledException {
		if (isWindows8()) {
			this.card.endExclusive();
		}
		ResponseAPDU responseApdu;
		int retriesLeft = -1;

		responseApdu = verifyPin(pin, retriesLeft);
		if (0x9000 != responseApdu.getSW()) {
			if (0x6983 == responseApdu.getSW()) {
				throw new CardException("This eID card is blocked!");
			}
			if (0x63 != responseApdu.getSW1()) {
				throw new CardException("PIN verification error.");
			}
			retriesLeft = responseApdu.getSW2() & 0xf;
			System.out.println("retries left: " + retriesLeft);
		}

		if (isWindows8()) {
			this.card.beginExclusive();
		}

		return true;
	}
	private ResponseAPDU verifyPin(char[] pin, int retriesLeft) throws CardException, UserCancelledException {
		byte[] verifyData = new byte[] { (byte) (0x20 | pin.length), (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		for (int idx = 0; idx < pin.length; idx += 2) {
			char digit1 = pin[idx];
			char digit2;
			if (idx + 1 < pin.length) {
				digit2 = pin[idx + 1];
			} else {
				digit2 = '0' + 0xf;
			}
			byte value = (byte) (byte) ((digit1 - '0' << 4) + (digit2 - '0'));
			verifyData[idx / 2 + 1] = value;
		}
		Arrays.fill(pin, (char) 0); // minimize exposure

		System.out.println("verifying PIN...");
		CommandAPDU verifyApdu = new CommandAPDU(0x00, 0x20, 0x00, 0x01, verifyData);
		try {
			ResponseAPDU responseApdu = transmit(verifyApdu);
			return responseApdu;
		} finally {
			Arrays.fill(verifyData, (byte) 0); // minimize exposure
		}
	}

	// ----- Platform check --------------------------------------------------------------------------------------------
	public boolean isOSX() {
		String osName = System.getProperty("os.name");
		return osName.contains("OS X");
	}
	public boolean isWindows8() {
		String osName = System.getProperty("os.name");
		boolean win8 = osName.contains("Windows 8");
		if (win8) {
			return true;
		}
		boolean win10 = osName.contains("Windows 10");
		if (win10) {
			return true;
		}
		return false;
	}
}
