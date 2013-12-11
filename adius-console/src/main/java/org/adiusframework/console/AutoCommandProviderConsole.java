package org.adiusframework.console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.adiusframework.util.AbstractStoppableThread;
import org.adiusframework.util.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoCommandProviderConsole extends CommandConsole {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AutoCommandProviderConsole.class);

	private CommandProviderThread thread;

	public AutoCommandProviderConsole() {
		thread = new CommandProviderThread();
		thread.setDaemon(true);
	}

	@Override
	protected void parseCommand(String command) {
		// nothing to do her
	}

	protected void parseInternalCommand(String command) {
		super.parseCommand(command);
	}

	@Override
	public void stop() {
		thread.stopThread();
		super.stop();
	}

	@Override
	public void init(String[] runtimeConfig) {
		super.init(runtimeConfig);
		thread.start();
	}

	public static void main(String[] args) {
		new AutoCommandProviderConsole().init(args);
	}

	protected class CommandProviderThread extends AbstractStoppableThread {

		private List<DataPackage> packageList;

		private Map<UUID, DataPackage> finishedList;

		public CommandProviderThread() {
			setPackageList(new Vector<DataPackage>());
			setFinishedList(new HashMap<UUID, DataPackage>());
			getPackageList().add(new DataPackage("D:\\Temp\\vtktest.vtk", "vtk", true));
			getPackageList().add(new DataPackage("ftp://default:default@localhost:21/Temp/vtktest.vtk", "vtk", true));
			getPackageList().add(new DataPackage("D:\\Temp\\inptest.zip", "inp", false));
		}

		public List<DataPackage> getPackageList() {
			return packageList;
		}

		public void setPackageList(List<DataPackage> packageList) {
			this.packageList = packageList;
		}

		public Map<UUID, DataPackage> getFinishedList() {
			return finishedList;
		}

		public void setFinishedList(Map<UUID, DataPackage> finishedList) {
			this.finishedList = finishedList;
		}

		@Override
		protected void runInLoop() {
			if (getPackageList().size() < 1)
				return;

			// first lets randomly decide if we do an extraction or integration
			// job
			LOGGER.debug("Roll the dice, we have " + getFinishedList().size() + " items in the extraction list");
			boolean integrate = (Math.random() * 100.0) > 50.0;
			if (getFinishedList().size() < 1)
				integrate = true;
			LOGGER.debug("It's an " + (integrate ? "integration" : "extraction"));

			// now lets build a command
			String command = null;

			// integration
			if (integrate) {
				int testPackageNr = (int) Math.round((getPackageList().size() - 1) * Math.random());
				DataPackage testPackage = getPackageList().get(testPackageNr);
				UUID uuid = UUID.randomUUID();
				command = new StringBuilder().append("integrate fem ").append(testPackage.getFormat()).append(" ")
						.append(uuid.toString()).append(" ").append(uuid.toString()).append(" ")
						.append(testPackage.getPath()).toString();
				if (testPackage.isExtractable())
					getFinishedList().put(uuid, testPackage);
			}

			// extraction
			else {
				int uuidNr = (int) Math.round((getFinishedList().size() - 1) * Math.random());
				int index = 0;
				UUID uuid = null;
				for (UUID key : getFinishedList().keySet()) {
					if (index == uuidNr) {
						uuid = key;
						break;
					}
					index++;
				}
				if (uuid != null) {
					DataPackage testPackage = getFinishedList().get(uuid);
					command = new StringBuilder()
							.append("extract fem ")
							.append(testPackage.getFormat())
							.append(" ")
							.append(uuid.toString())
							.append(" ")
							.append(uuid.toString())
							.append(" ")
							.append("D:\\Temp\\Result\\"
									+ FileUtil.generateUniqueFileName("tmp",
											FileUtil.getFileExtension(testPackage.getPath()))).toString();
				}
			}

			// now lets have some fun and fire the command
			LOGGER.info("Firing command '" + command + "'");
			AutoCommandProviderConsole.this.parseInternalCommand(command);

			// lets wait a little bit
			try {
				long sleepTime = Math.round(Math.random() * 80000);
				LOGGER.debug("Waiting for " + sleepTime + " milliseconds");
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				LOGGER.debug("Sleep interruped " + e.getMessage());
			}
		}
	}
}
