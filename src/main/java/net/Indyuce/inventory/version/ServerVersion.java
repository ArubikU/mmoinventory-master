package net.Indyuce.inventory.version;

public class ServerVersion {
	private final String version;

	public ServerVersion(Class<?> clazz) {
		version = clazz.getPackage().getName().replace(".", ",").split(",")[3];
	}

	@Override
	public String toString() {
		return version;
	}
}
