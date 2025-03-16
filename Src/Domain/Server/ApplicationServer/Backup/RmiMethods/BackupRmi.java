package Src.Domain.Server.ApplicationServer.Backup.RmiMethods;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import Src.Domain.Server.Server;
import Src.Domain.Server.Message.Message;
import Utils.Logger;

public class BackupRmi extends UnicastRemoteObject implements BackupRmiInterface {
	private Server serverCore;
	private Logger logger;

	public BackupRmi(Server serverCore, Logger logger) throws RemoteException {
		super();

		this.serverCore = serverCore;
		this.logger = logger;
	}

	@Override
	public void synchronizeDatabase() throws RemoteException {
		// Implementation here
	}

	@Override
	public void removeServiceOrder(Message message) throws RemoteException {
		this.serverCore.deleteServiceOrder(message);

		
	}

	@Override
	public void updateServiceOrder(Message message) throws RemoteException {
		this.serverCore.updateServiceOrder(message);
	}

	@Override
	public void addServiceOrder(Message message) throws RemoteException {
		this.serverCore.storeServiceOrder(message);
	}

	@Override
	public void synchronizeLogs(String message, String type) throws RemoteException {
		switch (type) {
			case "info":
				logger.info(message);
				break;

			case "warning":
				logger.warning(message);
				break;

			case "error":
				logger.error(message);
				break;
		
			default:
				break;
		}
	}
}
