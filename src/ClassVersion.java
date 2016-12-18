

import java.util.HashMap;
import java.util.Map;

/**
 * Major Version Numbers:
 * <pre>
 * J2SE 8 = 52 (0x34 hex),
 * J2SE 7 = 51 (0x33 hex),
 * J2SE 6.0 = 50 (0x32 hex),
 * J2SE 5.0 = 49 (0x31 hex),
 * JDK 1.4 = 48 (0x30 hex),
 * JDK 1.3 = 47 (0x2F hex),
 * JDK 1.2 = 46 (0x2E hex),
 * JDK 1.1 = 45 (0x2D hex).
 * </pre>
 * 
 * @author bill
 */
enum ClassVersion {
	JAVA_1_1(45, 1.1f),
	JAVA_1_2(46, 1.2f),
	JAVA_1_3(47, 1.3f),
	JAVA_1_4(48, 1.4f),
	JAVA_5(49, 5),
	JAVA_6(50, 6),
	JAVA_7(51, 7),
	JAVA_8(52, 8);
	
	private int majorVersion;
	private float[] productVersionNumbers;
	
	static private Map<Integer,ClassVersion> majorVersionMap = new HashMap<Integer,ClassVersion>();
	static private Map<Float,ClassVersion> productVersionMap = new HashMap<Float,ClassVersion>();
	
	static {
		for (ClassVersion v : values()) {
			majorVersionMap.put(v.majorVersion, v);
			for (float productVersion : v.getProductVersions()) {
				productVersionMap.put(productVersion, v);
			}
		}
	}
	
	private ClassVersion(int majorVersion, float ... productVersionNumbers) {
		this.majorVersion = majorVersion;
		this.productVersionNumbers = productVersionNumbers;
	}
	
	public int getMajorVersion() {
		return majorVersion;
	}
	
	public float getProductVersion() {
		return productVersionNumbers[0];
	}
	
	public String getProductName() {
		return "Java " + productVersionNumbers[0];
	}
	
	public float[] getProductVersions() {
		return productVersionNumbers;
	}
	
	public static ClassVersion lookupByMajorVersion(int majorVersion) {
		return majorVersionMap.get(majorVersion);
	}
	
	public static ClassVersion lookupByProductVersion(float productVersion) {
		return productVersionMap.get(productVersion);
	}
	
	@Override
	public String toString() {
		return String.format("%s (major version: %d)", getProductName(), majorVersion);
	}
}
